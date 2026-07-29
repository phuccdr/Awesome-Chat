package com.rikkeisoft.awesome.alluser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.project.core.utils.loadImage
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.friends.databinding.ItemAlphabetHeaderBinding
import com.rikkeisoft.awesome.friends.databinding.ItemUserBinding
import com.rikkeisoft.awesome.model.UserUI
import com.rikkeisoft.awesome.model.UserUI.AlphabetHeader
import com.rikkeisoft.awesome.model.UserUI.UserItem

class UserAdapter(private val onClick: (userId: String) -> Unit) :
    PagingDataAdapter<UserUI, ViewHolder>(
        DiffCallback()
    ) {
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is UserUI.UserItem) USER_TYPE
        else HEADER_TYPE

    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> {
                val binding = ItemUserBinding.inflate(inflater, parent, false)
                UserViewHolder(binding)
            }

            2 -> {
                val binding = ItemAlphabetHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        val item = getItem(position)
        when (holder) {
            is UserViewHolder -> holder.bind(item as UserItem)
            is HeaderViewHolder -> holder.bind(item as AlphabetHeader)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemAlphabetHeaderBinding) :
        ViewHolder(binding.root) {
        fun bind(item: AlphabetHeader) {
            binding.tvAlphabet.text = item.title
        }
    }

    inner class UserViewHolder(val binding: ItemUserBinding) : ViewHolder(binding.root) {
        fun bind(item: UserItem) {
            binding.tvUserName.text = item.username
            binding.ivAvatar.loadImage(item.avatar, true)
            binding.btnAddFriend.isVisible = !item.isFriend
            binding.btnAddFriend.setOnSafeClickListener {
                onClick(item.id)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UserUI>() {
        override fun areItemsTheSame(oldItem: UserUI, newItem: UserUI): Boolean {
            return if (oldItem is UserItem && newItem is UserItem) {
                oldItem.id == newItem.id
            } else if (oldItem is AlphabetHeader && newItem is AlphabetHeader) {
                oldItem.title == newItem.title
            } else {
                false
            }
        }

        override fun areContentsTheSame(
            oldItem: UserUI, newItem: UserUI
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val USER_TYPE = 1
        private const val HEADER_TYPE = 2
    }

}

