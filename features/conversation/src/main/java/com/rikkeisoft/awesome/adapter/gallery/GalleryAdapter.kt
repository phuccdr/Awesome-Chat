package com.rikkeisoft.awesome.adapter.gallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.loadImage
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.ItemGalleryImageBinding
import com.rikkeisoft.awesome.model.GalleryImage

class GalleryAdapter(
    private val onClick: (GalleryImage) -> Unit
) : PagingDataAdapter<GalleryImage, GalleryAdapter.VH>(DIFF) {
    private var selectedUris: List<Uri> = emptyList()

    fun submitSelection(selection: List<Uri>) {
        selectedUris = selection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemGalleryImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class VH(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GalleryImage) {
            binding.imageThumb.loadImage(item.uri, placeHolder = R.drawable.ic_place_holder)
            val index = selectedUris.indexOf(item.uri)
            val isSelected = index >= 0
            binding.selectedOverlay.isVisible = isSelected
            binding.badgeNumber.isVisible = isSelected
            if (isSelected) binding.badgeNumber.text = (index + 1).toString()

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<GalleryImage>() {
            override fun areItemsTheSame(a: GalleryImage, b: GalleryImage) = a.id == b.id
            override fun areContentsTheSame(a: GalleryImage, b: GalleryImage) = a == b
        }
    }
}
