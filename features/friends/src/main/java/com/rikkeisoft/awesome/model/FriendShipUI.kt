package com.rikkeisoft.awesome.model

import com.google.firebase.Timestamp
import com.project.core.model.firebase.FriendShipStatus
import com.project.core.model.firebase.User

sealed class FriendShipUI {
    data class FriendUI(
        val id: String? = null,
        val createdAt: Timestamp? = null,
        val friendId: String? = null,
        val friendFirstName: String? = null,
        val friend: User? = null,
        val status: FriendShipStatus = FriendShipStatus.ACTIVE,
        val conversationId: String? = null
    ) : FriendShipUI()

    data class AlphabetHeader(val title: String) : FriendShipUI()
}