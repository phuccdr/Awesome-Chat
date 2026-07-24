package com.project.core.model.firebase

import com.google.firebase.Timestamp

data class FriendShip(
    val id: String? = null,
    val createdAt: Timestamp? = null,
    val userId1: String? = null,
    val userId2: String? = null,
    val conversationId: String? = null,
    val status: FriendShipStatus = FriendShipStatus.ACTIVE,
)