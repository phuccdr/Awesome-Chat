package com.rikkeisoft.awesome.model

import com.project.core.model.firebase.User

data class ConversationChat(
    val id: String = "", val friend: User? = null, val currentlyUser: User? = null
)