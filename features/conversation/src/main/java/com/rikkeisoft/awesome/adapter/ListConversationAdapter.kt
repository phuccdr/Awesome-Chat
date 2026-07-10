package com.rikkeisoft.awesome.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.ItemConversationBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemLoadingFooterBinding
import com.rikkeisoft.awesome.model.ConversationItem

class ListConversationAdapter :
    ListAdapter<ConversationItem, RecyclerView.ViewHolder>(ConversationDiffUtil()) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ConversationItem.ConversationUi -> R.layout.item_conversation
            is ConversationItem.LoadingFooter -> R.layout.item_loading_footer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_conversation -> {
                val binding = ItemConversationBinding.inflate(inflater, parent, false)
                ConversationViewHolder(binding)
            }

            R.layout.item_loading_footer -> {
                val binding = ItemLoadingFooterBinding.inflate(inflater, parent, false)
                ConversationLoadingFooterViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is ConversationViewHolder && item is ConversationItem.ConversationUi) {
            holder.bind(item)
        }
    }

    class ConversationLoadingFooterViewHolder(private val binding: ItemLoadingFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ConversationItem.ConversationUi) {
            if (item.unreadMessageCount > 0) {
                binding.frameAvatarUnread.visibility = View.VISIBLE
                binding.ivAvatar.visibility = View.INVISIBLE
                binding.tvBadge.text = item.unreadMessageCount.toString()
                binding.ivAvatarUnread.loadImage(item.avatarFriend, isCircle = true)
                binding.tvLastMessage.setTextColor(
                    ResourceUtils.getColor(com.project.core.R.color.text_primary)
                )
                binding.tvLastMessage.setTextAppearance(
                    binding.tvLastMessage.context, com.project.core.R.style.AppText_Lato_Bold_14
                )
            } else {
                binding.frameAvatarUnread.visibility = View.GONE
                binding.ivAvatar.visibility = View.VISIBLE
                binding.ivAvatar.loadImage(item.avatarFriend, isCircle = true)
                binding.tvLastMessage.setTextColor(
                    ResourceUtils.getColor(com.project.core.R.color.text_gray_secondary)
                )
                binding.tvLastMessage.setTextAppearance(
                    binding.tvLastMessage.context, com.project.core.R.style.AppText_Lato_Medium_14
                )
            }
            if (item.isLastMessageSender) {
                binding.tvLastMessage.text =
                    ResourceUtils.getString(R.string.you_last_message, item.lastMessage)
            } else {
                binding.tvLastMessage.text = item.lastMessage
            }

            binding.tvFriendName.text = item.friendName
            binding.tvLastTime.text = item.lastUpdate
        }
    }
}