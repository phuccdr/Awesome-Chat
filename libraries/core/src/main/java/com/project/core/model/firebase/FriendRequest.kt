package com.project.core.model.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class FriendRequest(
    @DocumentId val id: String? = null,
    val senderId: String = "",
    val receiverId: String = "",
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
    val createdAt: Timestamp? = null,
    val acceptedAt: Timestamp? = null
)
