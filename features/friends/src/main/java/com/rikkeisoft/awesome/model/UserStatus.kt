package com.rikkeisoft.awesome.model

sealed class UserStatus {
    data class NotFriend(val receiverId: String) : UserStatus()

    data class RequestSent(val friendRequestId: String, val receivedId: String) : UserStatus()

    data class Friend(val conversationId: String) : UserStatus()
}