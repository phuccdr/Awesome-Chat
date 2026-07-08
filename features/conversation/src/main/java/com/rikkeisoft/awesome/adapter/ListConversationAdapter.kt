package com.rikkeisoft.awesome.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.ItemConversationBinding
import com.rikkeisoft.awesome.model.ConversationItem

class ListConversationAdapter : ListAdapter<ConversationItem, RecyclerView.ViewHolder>(ConversationDiffUtil()) {

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
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is ConversationViewHolder && item is ConversationItem.ConversationUi) {
            holder.bind(item)
        }
    }

    class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ConversationItem.ConversationUi) {
            binding.tvFriendName.text = item.friendName
            binding.tvLastMessage.text = item.lastMessage
            // Bind
        }
    }
}