package com.project.core.model.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Message(
    @DocumentId val id: String = "",
    val content: String = "",
    val createdAt: Timestamp? = null,
    val imageUrls: List<String>? = null,
    val stickerId: String? = null,
    val stickerUrl: String? = null,
    val seen: Boolean = false,
    val senderId: String = "",
    val type: MessageType? = MessageType.TEXT,
    val receiverId: String? = "",
    val conversationId: String? = ""
)