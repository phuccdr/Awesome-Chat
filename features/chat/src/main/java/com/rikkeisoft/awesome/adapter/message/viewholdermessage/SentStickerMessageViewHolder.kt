package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.format
import com.project.core.utils.loadImage
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.chat.databinding.ItemSentStickerMessageBinding
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class SentStickerMessageViewHolder(
    private val binding: ItemSentStickerMessageBinding,
    private val onMessageClick: (itemId: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageItem.StickerMessage) {
        binding.apply {
            ivSticker.loadImage(urlImage = item.stickerUrl)
            tvTime.text = item.createdAt.format()
            tvTime.visibility =
                if (item.isSelected || item.messagePosition == MessagePosition.BOTTOM || item.messagePosition == MessagePosition.SINGLE) {
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
