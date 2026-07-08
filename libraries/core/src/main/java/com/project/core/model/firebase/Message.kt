package com.project.core.model.firebase

import com.google.firebase.Timestamp

data class Message(
    val content: String = "",
    val createdAt: Timestamp? = null,
    val imageUrl: String? = null,
    val seen: Boolean = false,
    val senderId: String = "",
    val type: MessageType = MessageType.TEXT
)