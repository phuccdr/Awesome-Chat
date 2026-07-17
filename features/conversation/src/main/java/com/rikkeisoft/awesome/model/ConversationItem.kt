package com.rikkeisoft.awesome.model

import com.google.firebase.Timestamp

sealed class ConversationItem {
    data class ConversationUi(
        val id: String,
        val friendName: String = "",
        val avatarFriend: String = "",
        val lastMessage: String = "",
        val lastUpdate: Timestamp? = null,
        val isLastMessageSender: Boolean = false,
        val unreadMessageCount: Int = 0
    ) : ConversationItem()

    object LoadingFooter : ConversationItem()
}