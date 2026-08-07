package com.rikkeisoft.awesome.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

sealed class MessageItem {
    abstract class Message(
        @DocumentId open val id: String = "",
        open val isMine: Boolean = false,
        open val avatarFriend: String = "",
        open val createdAt: Timestamp? = null,
        open val status: SendStatus = SendStatus.SENT,
        open val isSelected: Boolean = false,
        open val messagePosition: MessagePosition = MessagePosition.SINGLE,
        open val senderId: String = ""
    ) : MessageItem()

    data class TextMessage(
        override val id: String = "",
        override val isMine: Boolean = false,
        override val avatarFriend: String = "",
        override val createdAt: Timestamp? = null,
        override val status: SendStatus = SendStatus.SENT,
        override val isSelected: Boolean = false,
        val content: String = "",
        val isSeen: Boolean = true,
        override val senderId: String = "",
        override val messagePosition: MessagePosition = MessagePosition.SINGLE,
    ) : Message(
        id = id,
        isMine = isMine,
        avatarFriend = avatarFriend,
        createdAt = createdAt,
        status = status,
        isSelected = isSelected,
        messagePosition = messagePosition,
        senderId = senderId
    )

    data class ImageMessage(
        override val id: String,
        override val status: SendStatus = SendStatus.SENT,
        override val avatarFriend: String = "",
        override val isMine: Boolean = false,
        override val createdAt: Timestamp? = null,
        override var isSelected: Boolean = false,
        override val messagePosition: MessagePosition,
        val imageUrls: List<String> = emptyList(),
        override val senderId: String = "",
    ) : Message(
        id = id,
        isMine = isMine,
        avatarFriend = avatarFriend,
        createdAt = createdAt,
        status = status,
        isSelected = isSelected,
        messagePosition = messagePosition,
        senderId = senderId
    )

    data class StickerMessage(
        override val id: String,
        override val status: SendStatus = SendStatus.SENT,
        override val avatarFriend: String = "",
        override val isMine: Boolean = false,
        override val createdAt: Timestamp? = null,
        override var isSelected: Boolean = false,
        override val messagePosition: MessagePosition,
        override val senderId: String = "",
        val stickerId: String = "",
        val stickerUrl: String = ""
    ) : Message(
        id = id,
        isMine = isMine,
        avatarFriend = avatarFriend,
        createdAt = createdAt,
        status = status,
        isSelected = isSelected,
        messagePosition = messagePosition,
        senderId = senderId
    )

    data class DateHeader(
        val date: LocalDate,
    ) : MessageItem()

}