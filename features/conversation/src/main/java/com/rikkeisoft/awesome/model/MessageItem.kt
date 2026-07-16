package com.rikkeisoft.awesome.model

sealed class MessageItem {
    abstract class Message(
        open val id: String = "",
        open val isMine: Boolean = false,
        open val avatarFriend: String = "",
        open val createAt: String = "",
        open val status: SendStatus = SendStatus.SENT,
        open val isSelected: Boolean = false,
        open val messagePosition: MessagePosition = MessagePosition.SINGLE
    ) : MessageItem()

    data class TextMessage(
        override val id: String = "",
        override val isMine: Boolean = false,
        override val avatarFriend: String = "",
        override val createAt: String = "",
        override val status: SendStatus = SendStatus.SENT,
        override val isSelected: Boolean = false,
        val content: String = "",
        val isSeen: Boolean = true,
        val senderId: String = "",
        override val messagePosition: MessagePosition = MessagePosition.SINGLE,
    ) : Message(
        id = id,
        isMine = isMine,
        avatarFriend = avatarFriend,
        createAt = createAt,
        status = status,
        isSelected = isSelected,
        messagePosition = messagePosition
    )

    data class ImageMessage(
        override val id: String,
        override val status: SendStatus = SendStatus.SENT,
        override val avatarFriend: String = "",
        override val isMine: Boolean = false,
        override val createAt: String = "",
        override var isSelected: Boolean = false,
        override val messagePosition: MessagePosition,
        val imageUrls: List<String> = emptyList(),
        val createdAt: String = "",
        val senderId: String = "",
    ) : Message(
        id = id,
        isMine = isMine,
        avatarFriend = avatarFriend,
        createAt = createAt,
        status = status,
        isSelected = isSelected,
        messagePosition = messagePosition
    )

    data class StickerMessage(
        override val id: String,
        override val status: SendStatus = SendStatus.SENT,
        override val avatarFriend: String = "",
        override val isMine: Boolean = false,
        override val createAt: String = "",
        override var isSelected: Boolean = false,
        override val messagePosition: MessagePosition,
        val stickerId: String = "",
        val createdAt: String = "",
        val senderId: String = "",
    ) : Message(
        id = id,
        isMine = isMine,
        avatarFriend = avatarFriend,
        createAt = createAt,
        status = status,
        isSelected = isSelected,
        messagePosition = messagePosition
    )

    data class DateHeader(
        val time: String,
    ) : MessageItem()

}