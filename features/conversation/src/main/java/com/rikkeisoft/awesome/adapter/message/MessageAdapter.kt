package com.rikkeisoft.awesome.adapter.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rikkeisoft.awesome.adapter.message.viewholdermessage.HeaderTimeViewHolder
import com.rikkeisoft.awesome.adapter.message.viewholdermessage.ReceivedImageMessageViewHolder
import com.rikkeisoft.awesome.adapter.message.viewholdermessage.ReceivedStickerMessageViewHolder
import com.rikkeisoft.awesome.adapter.message.viewholdermessage.ReceivedTextMessageViewHolder
import com.rikkeisoft.awesome.adapter.message.viewholdermessage.SentImageMessageViewHolder
import com.rikkeisoft.awesome.adapter.message.viewholdermessage.SentStickerMessageViewHolder
import com.rikkeisoft.awesome.adapter.message.viewholdermessage.SentTextMessageViewHolder
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.ItemHeaderTimeMessageBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemReceivedImageMessageBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemReceivedStickerMessageBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemReceivedTextMessageBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemSentImageMessageBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemSentStickerMessageBinding
import com.rikkeisoft.awesome.conversation.databinding.ItemSentTextMessageBinding
import com.rikkeisoft.awesome.model.MessageItem

class MessageAdapter(
    private val onMessageClick: () -> Unit,
    private val onImageClick: (imageUrl: String) -> Unit = {}
) : ListAdapter<MessageItem, RecyclerView.ViewHolder>(MessageDiffUtil()) {
    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is MessageItem.TextMessage -> {
                if (item.isMine) {
                    R.layout.item_sent_text_message
                } else {
                    R.layout.item_received_text_message
                }
            }

            is MessageItem.ImageMessage -> {
                if (item.isMine) {
                    R.layout.item_sent_image_message
                } else {
                    R.layout.item_received_image_message
                }
            }

            is MessageItem.StickerMessage -> {
                if (item.isMine) {
                    R.layout.item_sent_sticker_message
                } else {
                    R.layout.item_received_sticker_message
                }
            }

            is MessageItem.DateHeader -> {
                R.layout.item_header_time_message
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_received_text_message -> {
                val binding = ItemReceivedTextMessageBinding.inflate(inflater, parent, false)
                ReceivedTextMessageViewHolder(binding)
            }

            R.layout.item_sent_text_message -> {
                val binding = ItemSentTextMessageBinding.inflate(inflater, parent, false)
                SentTextMessageViewHolder(binding)
            }

            R.layout.item_received_image_message -> {
                val binding = ItemReceivedImageMessageBinding.inflate(inflater, parent, false)
                ReceivedImageMessageViewHolder(
                    binding, onImageClick = onImageClick, onMessageClick = onMessageClick
                )
            }

            R.layout.item_sent_image_message -> {
                val binding = ItemSentImageMessageBinding.inflate(inflater, parent, false)
                SentImageMessageViewHolder(
                    binding, onImageClick = onImageClick, onMessageClick = onMessageClick
                )
            }

            R.layout.item_header_time_message -> {
                val binding = ItemHeaderTimeMessageBinding.inflate(inflater, parent, false)
                HeaderTimeViewHolder(binding)
            }

            R.layout.item_received_sticker_message -> {
                val binding = ItemReceivedStickerMessageBinding.inflate(inflater, parent, false)
                ReceivedStickerMessageViewHolder(binding, onMessageClick = onMessageClick)
            }

            R.layout.item_sent_sticker_message -> {
                val binding = ItemSentStickerMessageBinding.inflate(inflater, parent, false)
                SentStickerMessageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        val item = getItem(position)
        when {
            item is MessageItem.TextMessage && holder is ReceivedTextMessageViewHolder -> {
                holder.bind(item)
            }

            item is MessageItem.TextMessage && holder is SentTextMessageViewHolder -> {
                holder.bind(item)
            }

            item is MessageItem.ImageMessage && holder is ReceivedImageMessageViewHolder -> {
                holder.bind(item)
            }

            item is MessageItem.ImageMessage && holder is SentImageMessageViewHolder -> {
                holder.bind(item)
            }

            item is MessageItem.StickerMessage && holder is ReceivedStickerMessageViewHolder -> {
                holder.bind(item)
            }

            item is MessageItem.StickerMessage && holder is SentStickerMessageViewHolder -> {
                holder.bind(item)
            }

            item is MessageItem.DateHeader && holder is HeaderTimeViewHolder -> {
                holder.bind(item.time)
            }
        }
    }

    fun getItemAt(position: Int) = getItem(position)
}