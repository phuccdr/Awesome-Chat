package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.format
import com.project.core.utils.loadImage
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.conversation.databinding.ItemReceivedStickerMessageBinding
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class ReceivedStickerMessageViewHolder(
    private val binding: ItemReceivedStickerMessageBinding,
    private val onMessageClick: (itemId: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MessageItem.StickerMessage) {
        binding.apply {
            // Xử lý hiển thị Avatar: chỉ hiển thị ở tin nhắn đầu tiên hoặc tin nhắn đơn lẻ trong nhóm
            if (item.messagePosition == MessagePosition.SINGLE || item.messagePosition == MessagePosition.TOP) {
                imvAvatar.visibility = View.VISIBLE
                imvAvatar.loadImage(urlImage = item.avatarFriend, isCircle = true)
            } else {
                imvAvatar.visibility = View.INVISIBLE
            }

            ivSticker.loadImage(urlImage = item.stickerId)

            tvTime.text = item.createdAt.format()
            tvTime.visibility = if (item.isSelected || item.messagePosition == MessagePosition.BOTTOM || item.messagePosition == MessagePosition.SINGLE) {
                View.VISIBLE
            } else {
                View.GONE
            }

            root.setOnSafeClickListener {
                onMessageClick(item.id)
            }
        }
    }
}
