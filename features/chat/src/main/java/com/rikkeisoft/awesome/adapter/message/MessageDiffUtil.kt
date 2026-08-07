package com.rikkeisoft.awesome.adapter.message

import androidx.recyclerview.widget.DiffUtil
import com.rikkeisoft.awesome.model.MessageItem

class MessageDiffUtil : DiffUtil.ItemCallback<MessageItem>() {
    override fun areItemsTheSame(
        oldItem: MessageItem, newItem: MessageItem
    ): Boolean {
        return when {
            oldItem is MessageItem.DateHeader && newItem is MessageItem.DateHeader -> oldItem == newItem
            oldItem is MessageItem.Message && newItem is MessageItem.Message -> oldItem.id == newItem.id
            else -> oldItem == newItem
        }
    }

    override fun areContentsTheSame(
        oldItem: MessageItem, newItem: MessageItem
    ): Boolean {
        return oldItem == newItem
    }

}