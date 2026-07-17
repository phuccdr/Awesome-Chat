package com.rikkeisoft.awesome.adapter.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.format
import com.project.core.utils.loadImage2
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.setOnSafeClickListenerxoa123
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.ItemConversationBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemLoadingFooterBinding
import com.rikkeisoft.awesome.model.ConversationItem

class ConversationAdapter(private val onConversationClick: (conversationId: String) -> Unit) :
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
            holder.bind(item, onConversationClick)
        }
    }

    class ConversationLoadingFooterViewHolder(private val binding: ItemLoadingFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ConversationItem.ConversationUi, onConversationClick: (String) -> Unit) {
            binding.root.setOnSafeClickListenerxoa123 {
                onConversationClick(item.id)
            }
            if (item.unreadMessageCount > 0) {
                binding.frameAvatarUnread.visibility = View.VISIBLE
                binding.ivAvatar.visibility = View.INVISIBLE
                binding.tvBadge.text = item.unreadMessageCount.toString()
                binding.ivAvatarUnread.loadImage2(item.avatarFriend, isCircle = true)
                binding.tvLastMessage.setTextColor(
                    ResourceUtils.getColor(com.project.core.R.color.text_primary)
                )
                binding.tvLastMessage.setTextAppearance(
                    binding.tvLastMessage.context, com.project.core.R.style.AppText_Lato_Bold_14
                )
            } else {
                binding.frameAvatarUnread.visibility = View.GONE
                binding.ivAvatar.visibility = View.VISIBLE
                binding.ivAvatar.loadImage2(item.avatarFriend, isCircle = true)
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
            binding.tvLastTime.text = item.lastUpdate.format()
        }
    }
}