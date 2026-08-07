package com.rikkeisoft.awesome.adapter.sticker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.rikkeisoft.awesome.chat.databinding.ItemStickerBinding
import com.rikkeisoft.awesome.model.Sticker

class StickerAdapter(
    private val onClick: (Sticker) -> Unit
) : ListAdapter<Sticker, StickerAdapter.VH>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemStickerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemStickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Sticker) {
            binding.item = item
            binding.ivSticker.loadImage(item.url)
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Sticker>() {
            override fun areItemsTheSame(a: Sticker, b: Sticker) = a.id == b.id
            override fun areContentsTheSame(a: Sticker, b: Sticker) = a == b
        }
    }
}
