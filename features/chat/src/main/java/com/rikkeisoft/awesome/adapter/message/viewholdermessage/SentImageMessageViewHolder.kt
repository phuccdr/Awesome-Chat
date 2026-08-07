package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.format
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.adapter.message.MessageImageAdapter
import com.rikkeisoft.awesome.chat.databinding.ItemSentImageMessageBinding
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class SentImageMessageViewHolder(
    private val binding: ItemSentImageMessageBinding,
    private val onImageClick: (imageUrl: String) -> Unit,
    private val onMessageClick: (itemId: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageItem.ImageMessage) {
        binding.apply {
            val imageCount = item.imageUrls.size
            val spanCount = when {
                imageCount == 0 -> 1
                imageCount < 3 -> imageCount
                else -> 3
            }

            rvImage.layoutManager = GridLayoutManager(root.context, spanCount)
            val adapter = MessageImageAdapter { imageUrl ->
                onImageClick(imageUrl)
            }
            adapter.submitList(item.imageUrls)
            rvImage.adapter = adapter

            tvTime.text = item.createdAt.format()
            tvTime.visibility =
                if (item.isSelected || item.messagePosition == MessagePosition.BOTTOM || item.messagePosition == MessagePosition.SINGLE) View.VISIBLE else View.GONE
            root.setOnSafeClickListener {
                onMessageClick(item.id)
            }
        }
    }
}
