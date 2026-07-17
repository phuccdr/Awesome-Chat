package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.format
import com.project.core.utils.loadImage2
import com.rikkeisoft.awesome.conversation.databinding.ItemSentStickerMessageBinding
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class SentStickerMessageViewHolder(
    private val binding: ItemSentStickerMessageBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageItem.StickerMessage) {
        binding.apply {
            ivSticker.loadImage2(urlImage = item.stickerId)
            tvTime.text = item.createdAt.format()
            tvTime.visibility =
                if (item.isSelected || item.messagePosition == MessagePosition.BOTTOM || item.messagePosition == MessagePosition.SINGLE) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            root.setOnClickListener {
                item.isSelected = !item.isSelected
            }
        }
    }
}
