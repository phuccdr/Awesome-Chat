package com.project.core.model.firebase

import com.google.firebase.Timestamp

data class Conversation(
    val id: String = "",
    val lastMessage: String = "",
    val lastSenderId: String = "",
    val lastUpdate: Timestamp = Timestamp.now(),
    val members: List<String> = emptyList(),
    val unreadMessage: Map<String, Int> = emptyMap()
)