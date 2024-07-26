package com.bussatriaapp.data.repository
import android.net.Uri
import android.util.Log
import com.bussatriaapp.data.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    private val messagesCollection = firestore.collection("messages")

    fun getMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listenerRegistration = messagesCollection
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limitToLast(100)  // Limit to last 100 messages for performance
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun sendMessage(content: String, type: String = "text", imageUrl: String? = null) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val message = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderId = currentUser.uid,
                senderName = currentUser.displayName ?: "Anonymous",
                content = content,
                timestamp = Date(),
                imageUrl = imageUrl,
                type = type
            )
            try {
                messagesCollection.document(message.id).set(message).await()
            } catch (e: FirebaseFirestoreException) {
                Log.e("ChatRepository", "Error sending message", e)
                // Handle the exception appropriately
            }
        } else {
            Log.e("ChatRepository", "User is not authenticated")
            // Handle unauthenticated state appropriately
        }
    }

    suspend fun sendImage(imageUri: Uri): String {
        val storageRef = storage.reference.child("chat_images/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(imageUri)
        val imageUrl = uploadTask.await().storage.downloadUrl.await().toString()
        sendMessage("Image", "image", imageUrl)
        return imageUrl
    }
}