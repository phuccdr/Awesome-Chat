package com.rikkeisoft.awesome.model

import com.google.firebase.firestore.DocumentReference
import com.project.core.model.firebase.Message
import com.project.core.model.firebase.User

data class SearchMessage(
    val conversationRef: DocumentReference? = null,
    val messages: List<Message> = emptyList(),
    val user: User? = null
)
