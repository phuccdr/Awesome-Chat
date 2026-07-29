package com.rikkeisoft.awesome.model

import com.google.firebase.Timestamp
import com.project.core.model.firebase.User

data class FriendRequestUI(
    val id: String? = null, val sender: User? = null, val createdAt: Timestamp? = null
)