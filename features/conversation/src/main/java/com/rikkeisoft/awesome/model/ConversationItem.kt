package com.rikkeisoft.awesome.model

sealed class ConversationItem {
    data class ConversationUi(
        val id: String,
        val friendName: String = "",
        val avatarFriend: String = "",
        val lastMessage: String = "",
        val lastUpdate: String = "",
        val lastSender: String = "",
        val unreadMessageCount: Int = 0
    ) : ConversationItem()

    object LoadingFooter : ConversationItem()
}