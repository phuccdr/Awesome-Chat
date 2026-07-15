package com.rikkeisoft.awesome.adapter.conversation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.ItemSearchConversationBinding
import com.rikkeisoft.awesome.model.SearchMessage

class ConversationSearchAdapter :
    ListAdapter<SearchMessage, ConversationSearchAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSearchConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchMessage) {
            binding.apply {
                tvFriendName.text = item.user?.username
                tvCountMessage.text =
                    ResourceUtils.getString(R.string.message_match, item.messages.size)
                ivAvatar.loadImage(item.user?.avatar, true)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SearchMessage>() {
        override fun areItemsTheSame(oldItem: SearchMessage, newItem: SearchMessage): Boolean {
            return oldItem.conversationRef?.path == newItem.conversationRef?.path
        }

        override fun areContentsTheSame(oldItem: SearchMessage, newItem: SearchMessage): Boolean {
            return oldItem == newItem
        }
    }
}