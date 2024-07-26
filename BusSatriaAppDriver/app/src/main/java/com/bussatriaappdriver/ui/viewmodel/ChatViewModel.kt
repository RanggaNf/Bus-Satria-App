package com.bussatriaappdriver.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaappdriver.data.model.ChatMessage
import com.bussatriaappdriver.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
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

    fun sendMessage(content: String, senderId: String, senderName: String) {
        viewModelScope.launch {
            val message = ChatMessage(
                senderId = senderId,
                senderName = senderName,
                content = content,
                timestamp = Date()
            )
            repository.sendMessage(message)
        }
    }

    fun sendImage(imageUri: Uri, senderId: String, senderName: String) {
        viewModelScope.launch {
            repository.sendImage(imageUri, senderId, senderName)
        }
    }
}