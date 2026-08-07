package com.rikkeisoft.awesome.friendrequest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.rikkeisoft.awesome.friends.databinding.ItemSentFriendRequestBinding
import com.rikkeisoft.awesome.model.FriendRequestUI
import timber.log.Timber

class SentFriendRequestAdapter(private val onCancelClick: (FriendRequestUI) -> Unit) :
    PagingDataAdapter<FriendRequestUI, SentFriendRequestAdapter.SentFriendRequestViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SentFriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSentFriendRequestBinding.inflate(inflater, parent, false)
        return SentFriendRequestViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SentFriendRequestViewHolder, position: Int
    ) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class SentFriendRequestViewHolder(private val binding: ItemSentFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendRequestUI) {
            with(binding) {
                ivAvatar.loadImage(
                    item.receiver?.avatar,
                    isCircle = true,
                    placeHolder = com.project.core.R.drawable.ic_avatar_default
                )
                tvUserName.text = item.receiver?.username
                Timber.d(item.receiver.toString())
                btnCancel.setOnClickListener {
                    onCancelClick(item)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FriendRequestUI>() {
        override fun areItemsTheSame(
            oldItem: FriendRequestUI, newItem: FriendRequestUI
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FriendRequestUI, newItem: FriendRequestUI
        ): Boolean {
            return oldItem == newItem
        }
    }
}
