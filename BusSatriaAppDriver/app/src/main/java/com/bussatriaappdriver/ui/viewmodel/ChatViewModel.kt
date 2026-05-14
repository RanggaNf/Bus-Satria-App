package com.bussatriaappdriver.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaappdriver.data.model.ChatMessage
import com.bussatriaappdriver.data.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        getMessages()
    }

    private fun getMessages() {
        viewModelScope.launch {
            repository.getMessages().collect {
                _messages.value = it
            }
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
            repository.sendImage(imageUri)
        }
    }
}