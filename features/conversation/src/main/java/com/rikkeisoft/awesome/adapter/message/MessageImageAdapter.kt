package com.rikkeisoft.awesome.adapter.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage2
import com.project.core.utils.setOnSafeClickListenerxoa123
import com.rikkeisoft.awesome.conversation.databinding.ItemImageMessageBinding

class MessageImageAdapter(
    private val onClick: (imageUrl: String) -> Unit
) : ListAdapter<String, MessageImageAdapter.ImageViewHolder>(object :
    DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: String, newItem: String
    ): Boolean {
        return oldItem == newItem
    }
}) {
    private val a: Int = 0
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ImageViewHolder {
        val binding = ItemImageMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder, position: Int
    ) {
        return holder.bind(getItem(position))
    }

    inner class ImageViewHolder(
        private val binding: ItemImageMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            binding.imvImage.loadImage2(imageUrl)
            binding.imvImage.setOnSafeClickListenerxoa123 { onClick(imageUrl) }
        }
    }
}