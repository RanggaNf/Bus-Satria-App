package com.bussatriaapp.data.model


import java.util.Date

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Date = Date(),
    val imageUrl: String? = null,
    val type: String = "text"
)