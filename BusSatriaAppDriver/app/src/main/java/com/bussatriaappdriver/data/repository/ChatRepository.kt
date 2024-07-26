package com.bussatriaappdriver.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.Log
import com.bussatriaappdriver.data.model.ChatMessage
import com.bussatriaappdriver.utils.PreferenceUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val messagesCollection = firestore.collection("messages")

    fun getMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listenerRegistration = messagesCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)  // Limit to last 100 messages for performance
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                trySend(messages.reversed())  // Reverse to show newest messages at the bottom
            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun sendMessage(message: ChatMessage) {
        messagesCollection.add(message)
    }

    suspend fun sendImage(imageUri: Uri, senderId: String, senderName: String): String {
        val storageRef = storage.reference.child("chat_images/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(imageUri)
        val imageUrl = uploadTask.await().storage.downloadUrl.await().toString()

        val message = ChatMessage(
            senderId = senderId,
            senderName = senderName,
            content = "Image",
            imageUrl = imageUrl,
            type = "image"
        )
        sendMessage(message)
        return imageUrl
    }
}