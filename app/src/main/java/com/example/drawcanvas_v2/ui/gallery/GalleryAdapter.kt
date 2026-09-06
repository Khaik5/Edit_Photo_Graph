package com.example.drawcanvas_v2.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawcanvas_v2.data.model.ImageItem
import com.example.drawcanvas_v2.databinding.ItemGalleryBinding

class GalleryAdapter(
    private val onClick: (ImageItem) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ImageViewHolder>() {
    private val items = mutableListOf<ImageItem>()
    fun setItems(newItems: List<ImageItem>
    ) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(
            binding
        )
    }
    override fun onBindViewHolder(
        holder: ImageViewHolder,
        position: Int
    ) {
        holder.bind(items[position]
        )
    }
    override fun getItemCount(): Int {
        return items.size
    }
    inner class ImageViewHolder(
        private val binding:
        ItemGalleryBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(
            item: ImageItem
        ) {
            Glide.with(binding.ivImage).load(item.image).centerCrop().into(binding.ivImage)
            binding.root.setOnClickListener {
                    onClick(item)
            }
        }
    }
}