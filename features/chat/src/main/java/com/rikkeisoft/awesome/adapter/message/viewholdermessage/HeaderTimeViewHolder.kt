package com.rikkeisoft.awesome.adapter.message.viewholdermessage

import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.toDisplayText
import com.rikkeisoft.awesome.chat.databinding.ItemHeaderTimeMessageBinding
import java.time.LocalDate

class HeaderTimeViewHolder(
    private val binding: ItemHeaderTimeMessageBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(time: LocalDate) {
        binding.tvTimeHeader.text = time.toDisplayText()
    }
}