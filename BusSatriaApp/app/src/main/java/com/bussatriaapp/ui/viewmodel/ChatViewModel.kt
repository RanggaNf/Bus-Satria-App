package com.bussatriaapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaapp.data.model.ChatMessage
import com.bussatriaapp.data.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bussatriaapp.MainActivity
import com.bussatriaapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
    private val channelId = "chat_notifications"
    private val notificationId = 1

    private val _needsNotificationPermission = MutableStateFlow(false)
    val needsNotificationPermission: StateFlow<Boolean> = _needsNotificationPermission

    init {
        createNotificationChannel()
        getMessages()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Chat Notifications"
            val descriptionText = "Notifications for new chat messages"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getMessages() {
        viewModelScope.launch {
            repository.getMessages().collect { newMessages ->
                val oldMessages = _messages.value
                _messages.value = newMessages

                val newMessagesOnly = newMessages.filter { it !in oldMessages }
                if (newMessagesOnly.isNotEmpty()) {
                    newMessagesOnly.forEach { latestMessage ->
                        if (latestMessage.senderId != FirebaseAuth.getInstance().currentUser?.uid) {
                            sendNotification(latestMessage)
                        }
                    }
                }
            }
        }
    }


    private fun sendNotification(message: ChatMessage) {
        if (checkNotificationPermission()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.busmerah)
                .setContentTitle("SATRIA Obrolan")
                .setContentText("${message.senderName}: ${message.content}")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            try {
                notificationManager.notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                _needsNotificationPermission.value = true
            }
        } else {
            _needsNotificationPermission.value = true
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
                val userName = userDoc.getString("name") ?: "Anonymous"
                repository.sendMessage(content, userName)
            }
        }
    }

    fun sendImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadImageToStorage(imageUri)
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
                    val userName = userDoc.getString("name") ?: "Anonymous"
                    repository.sendMessage("Image", userName, type = "image", imageUrl = imageUrl)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error uploading image", e)
            }
        }
    }


    fun onNotificationPermissionGranted() {
        _needsNotificationPermission.value = false
    }
    fun deleteMessage(message: ChatMessage) {
        viewModelScope.launch {
            repository.deleteMessage(message)
        }
    }
}
