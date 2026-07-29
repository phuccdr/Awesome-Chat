package com.rikkeisoft.awesome.friendslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.rikkeisoft.awesome.friends.databinding.ItemAlphabetHeaderBinding
import com.rikkeisoft.awesome.friends.databinding.ItemFriendBinding
import com.rikkeisoft.awesome.model.FriendShipUI

class FriendListAdapter : PagingDataAdapter<FriendShipUI, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FriendShipUI.FriendUI -> TYPE_FRIEND
            is FriendShipUI.AlphabetHeader -> TYPE_HEADER
            else -> {
                0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_FRIEND -> {
                val binding = ItemFriendBinding.inflate(inflater, parent, false)
                FriendViewHolder(binding)
            }

            TYPE_HEADER -> {
                val binding = ItemAlphabetHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is FriendViewHolder -> holder.bind(item as FriendShipUI.FriendUI)
            is HeaderViewHolder -> holder.bind(item as FriendShipUI.AlphabetHeader)
        }
    }

    class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendShipUI.FriendUI) {
            binding.apply {
                tvUserName.text = item.friend?.username
                ivAvatar.loadImage(item.friend?.avatar, isCircle = true)
            }
        }
    }

    class HeaderViewHolder(private val binding: ItemAlphabetHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendShipUI.AlphabetHeader) {
            binding.tvAlphabet.text = item.title
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FriendShipUI>() {
        override fun areItemsTheSame(oldItem: FriendShipUI, newItem: FriendShipUI): Boolean {
            return if (oldItem is FriendShipUI.FriendUI && newItem is FriendShipUI.FriendUI) {
                oldItem.id == newItem.id
            } else if (oldItem is FriendShipUI.AlphabetHeader && newItem is FriendShipUI.AlphabetHeader) {
                oldItem.title == newItem.title
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: FriendShipUI, newItem: FriendShipUI): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val TYPE_FRIEND = 0
        private const val TYPE_HEADER = 1
    }
}
