package com.rikkeisoft.awesome.ui.chat

import com.project.core.model.firebase.Message
import com.project.core.model.firebase.MessageType
import com.project.core.utils.toLocalDate
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition
import java.time.LocalDate

object MessageToMessageItemMapper {



    /**
     * Map list of messages to message items with date headers and proper positioning
     * @param messages List of Message objects from Firebase
     * @param currentUserId Current user's ID
     * @param friendAvatar Avatar URL of the friend
     * @return List of MessageItem with DateHeader and proper MessagePosition
     */
    fun mapMessagesToMessageItems(
        messages: List<Message>,
        currentUserId: String,
        friendAvatar: String = ""
    ): List<MessageItem> {
        if (messages.isEmpty()) return emptyList()

        val result = mutableListOf<MessageItem>()

        // Group messages by date
        val messagesByDate: LinkedHashMap<LocalDate,List<Message>> = groupMessagesByDate(messages)

        messagesByDate.forEach { (date, messagesOfDate) ->
            // Add date header
            result.add(MessageItem.DateHeader(date))

            // Process messages of the same date and calculate positions
            val messageItems = mutableListOf<MessageItem.Message>()
            var previousSenderId = ""
            val senderGroups = mutableListOf<List<Message>>()
            var currentGroup = mutableListOf<Message>()

            // Group by consecutive senders
            messagesOfDate.forEach { message ->
                if (previousSenderId.isEmpty() || previousSenderId == message.senderId) {
                    currentGroup.add(message)
                    previousSenderId = message.senderId
                } else {
                    senderGroups.add(currentGroup.toList())
                    currentGroup = mutableListOf(message)
                    previousSenderId = message.senderId
                }
            }
            if (currentGroup.isNotEmpty()) {
                senderGroups.add(currentGroup.toList())
            }

            // Map each sender group with appropriate positions
            senderGroups.forEach { group:List<Message> ->
                val positions = calculateMessagePositions(group.size)
                group.forEachIndexed { index, message ->
                    messageItems.add(
                        mapMessageToMessageItem(
                            message = message,
                            currentUserId = currentUserId,
                            friendAvatar = friendAvatar,
                            position = positions[index]
                        )
                    )
                }
            }

            result.addAll(messageItems)
        }
        return result
    }




    /**
     * Group messages by date using TimeUtils format
     */

    private fun groupMessagesByDate(messages: List<Message>): LinkedHashMap<LocalDate, List<Message>> {
        val grouped = LinkedHashMap<LocalDate, MutableList<Message>>()

        messages.forEach { message ->
            val day = message.createdAt?.toLocalDate()
            day?.let{
            grouped.getOrPut(it) { mutableListOf() }.add(message)}
        }
        return LinkedHashMap(grouped)
    }


    /**
     * Calculate message positions based on group size
     * Returns list of MessagePosition matching the group size
     */
    private fun calculateMessagePositions(groupSize: Int): List<MessagePosition> {
        return when (groupSize) {
            1 -> listOf(MessagePosition.SINGLE)
            2 -> listOf(MessagePosition.TOP, MessagePosition.BOTTOM)
            else -> {
                val positions = mutableListOf<MessagePosition>()
                positions.add(MessagePosition.TOP)
                repeat(groupSize - 2) {
                    positions.add(MessagePosition.MIDDLE)
                }
                positions.add(MessagePosition.BOTTOM)
                positions.toList()
            }
        }
    }

    /**
     * Map single Message to MessageItem based on its type
     */
     fun mapMessageToMessageItem(
        message: Message,
        currentUserId: String,
        friendAvatar: String,
        position: MessagePosition = MessagePosition.SINGLE
    ): MessageItem.Message {
        val isMine = message.senderId == currentUserId
        val senderId = message.senderId

        return when (message.type) {
            MessageType.IMAGE -> {
                val imageUrls = message.imageUrl?.let { listOf(it) } ?: emptyList()
                MessageItem.ImageMessage(
                    id = message.id,
                    isMine = isMine,
                    avatarFriend = if (isMine) "" else friendAvatar,
                    createdAt = message.createdAt,
                    messagePosition = position,
                    imageUrls = imageUrls,
                    senderId = senderId
                )
            }

            MessageType.VIDEO, MessageType.FILE -> {
                // Treat video and file as text message for now
                MessageItem.TextMessage(
                    id = message.id,
                    isMine = isMine,
                    avatarFriend = if (isMine) "" else friendAvatar,
                    createdAt = message.createdAt,
                    content = message.content,
                    isSeen = message.seen,
                    senderId = senderId,
                    messagePosition = position
                )
            }

            else -> {
                // Default to TextMessage for TEXT type or any other type
                MessageItem.TextMessage(
                    id = message.id,
                    isMine = isMine,
                    avatarFriend = if (isMine) "" else friendAvatar,
                    createdAt = message.createdAt,
                    content = message.content,
                    isSeen = message.seen,
                    senderId = senderId,
                    messagePosition = position
                )
            }
        }
    }
}

