package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.format
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.chat.databinding.ItemSentTextMessageBinding
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class SentTextMessageViewHolder(
    private val binding: ItemSentTextMessageBinding,
    private val onMessageClick: (itemId: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageItem.TextMessage) {
        binding.apply {
            tvMessage.bindWithTextMessage(
                isMine = item.isMine, messagePosition = item.messagePosition, content = item.content
            )

            if (item.isSelected || item.messagePosition == MessagePosition.BOTTOM || item.messagePosition == MessagePosition.SINGLE) {
                tvTime.text = item.createdAt.format()
                tvTime.visibility = View.VISIBLE

            } else {
                tvTime.visibility = View.GONE
            }
            root.setOnSafeClickListener {
                onMessageClick(item.id)
            }
        }
    }
}