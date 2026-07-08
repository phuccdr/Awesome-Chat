package com.rikkeisoft.awesome.adapter

import androidx.recyclerview.widget.DiffUtil
import com.rikkeisoft.awesome.model.ConversationItem

class ConversationDiffUtil : DiffUtil.ItemCallback<ConversationItem>() {
    override fun areItemsTheSame(
        oldItem: ConversationItem, newItem: ConversationItem
    ): Boolean {
        return if (oldItem is ConversationItem.ConversationUi && newItem is ConversationItem.ConversationUi) {
            oldItem.id == newItem.id
        } else oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ConversationItem, newItem: ConversationItem
    ): Boolean {
        return oldItem == newItem
    }

}