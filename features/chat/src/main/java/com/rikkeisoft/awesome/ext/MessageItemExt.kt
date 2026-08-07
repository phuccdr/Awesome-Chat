package com.rikkeisoft.awesome.ext

import com.google.firebase.Timestamp
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition
import com.rikkeisoft.awesome.model.SendStatus

fun MessageItem.Message.copyMessageItem(
    id: String = this.id,
    isMine: Boolean = this.isMine,
    avatarFriend: String = this.avatarFriend,
    createdAt: Timestamp? = this.createdAt,
    status: SendStatus = this.status,
    isSelected: Boolean = this.isSelected,
    messagePosition: MessagePosition = this.messagePosition,
    senderId: String = this.senderId,
): MessageItem.Message {
    return when (this) {
        is MessageItem.TextMessage -> copy(
            id = id,
            isMine = isMine,
            avatarFriend = avatarFriend,
            createdAt = createdAt,
            status = status,
            isSelected = isSelected,
            messagePosition = messagePosition,
            senderId = senderId,
        )

        is MessageItem.ImageMessage -> copy(
            id = id,
            isMine = isMine,
            avatarFriend = avatarFriend,
            createdAt = createdAt,
            status = status,
            isSelected = isSelected,
            messagePosition = messagePosition,
            senderId = senderId,
        )

        is MessageItem.StickerMessage -> copy(
            id = id,
            isMine = isMine,
            avatarFriend = avatarFriend,
            createdAt = createdAt,
            status = status,
            isSelected = isSelected,
            messagePosition = messagePosition,
            senderId = senderId,
        )

        else -> this
    }
}