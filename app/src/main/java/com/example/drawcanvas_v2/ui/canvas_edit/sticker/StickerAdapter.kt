package com.example.drawcanvas_v2.ui.canvas_edit.sticker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawcanvas_v2.databinding.ItemStickerBinding
import com.example.drawcanvas_v2.data.model.StickerItem

class StickerAdapter(
    private val onClick:
        (StickerItem) -> Unit
) : RecyclerView.Adapter<
        StickerAdapter.ViewHolder
        >() {

    private val items =
        mutableListOf<StickerItem>()

    fun submitList(
        list: List<StickerItem>
    ) {

        items.clear()
        items.addAll(list)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemStickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(
            items[position]
        )
    }

    override fun getItemCount() =
        items.size

    inner class ViewHolder(
        private val binding:
        ItemStickerBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: StickerItem
        ) {

            Glide.with(
                binding.ivSticker
            )
                .load(
                    "file:///android_asset/${item.path}"
                )
                .centerInside()
                .into(
                    binding.ivSticker
                )

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}