package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.format
import com.project.core.utils.loadImage2
import com.rikkeisoft.awesome.conversation.databinding.ItemReceivedTextMessageBinding
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class ReceivedTextMessageViewHolder(
    private val binding: ItemReceivedTextMessageBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageItem.TextMessage) {
        binding.apply {
            tvMessage.bindWithTextMessage(
                isMine = item.isMine, messagePosition = item.messagePosition, content = item.content
            )
            if (item.messagePosition == MessagePosition.SINGLE || item.messagePosition == MessagePosition.TOP) {
                imvAvatar.visibility = View.VISIBLE
                imvAvatar.loadImage2(urlImage = item.avatarFriend, isCircle = true)
            } else {
                imvAvatar.visibility = View.INVISIBLE
            }
            if (item.isSelected || item.messagePosition == MessagePosition.BOTTOM || item.messagePosition == MessagePosition.SINGLE) {
                tvTime.text = item.createdAt.format()
                tvTime.visibility = View.VISIBLE
            } else {
                tvTime.visibility = View.GONE
            }
        }
    }
}