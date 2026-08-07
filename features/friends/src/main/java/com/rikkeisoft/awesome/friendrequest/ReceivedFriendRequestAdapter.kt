package com.rikkeisoft.awesome.friendrequest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.friends.databinding.ItemReceivedFriendRequestBinding
import com.rikkeisoft.awesome.model.FriendRequestUI

class ReceivedFriendRequestAdapter(
    private val onAcceptFriendRequest: (friendRequestId: String?) -> Unit,
    private val onRejectFriendRequest: (friendRequestId: String?) -> Unit
) : PagingDataAdapter<FriendRequestUI, ReceivedFriendRequestAdapter.FriendRequestViewHolder>(
    DiffCallback()
) {
    var openedViewHolder: RecyclerView.ViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemReceivedFriendRequestBinding.inflate(inflater, parent, false)
        return FriendRequestViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FriendRequestViewHolder, position: Int
    ) {
        getItem(position)?.let {
            return holder.bind(it)
        }
    }

    inner class FriendRequestViewHolder(internal val binding: ItemReceivedFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root), SwipeRevealHolder {
        override val contentView: View get() = binding.layoutContent
        override val actionWidth: Float get() = binding.layoutAction.width.toFloat()

        fun bind(item: FriendRequestUI) {
            with(binding) {
                layoutContent.translationX = 0f
                ivAvatar.loadImage(item.sender?.avatar, isCircle = true)
                tvUserName.text = item.sender?.username
                btnAccept.setOnSafeClickListener {
                    onAcceptFriendRequest(item.id)
                }
                btnReject.setOnSafeClickListener {
                    onRejectFriendRequest(item.id)
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