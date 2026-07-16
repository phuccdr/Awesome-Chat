package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.rikkeisoft.awesome.conversation.databinding.ItemSentStickerMessageBinding
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class SentStickerMessageViewHolder(
    private val binding: ItemSentStickerMessageBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageItem.StickerMessage) {
        binding.apply {
            ivSticker.loadImage(urlImage = item.stickerId)
            tvTime.text = item.createAt
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
