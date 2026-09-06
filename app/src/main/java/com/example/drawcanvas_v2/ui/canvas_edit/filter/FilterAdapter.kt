package com.example.drawcanvas_v2.ui.canvas_edit.filter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawcanvas_v2.databinding.ItemFilterBinding
import com.example.drawcanvas_v2.utils.AnimationUtils
import com.example.drawcanvas_v2.utils.ImageFilterUtils

class FilterAdapter(
    private val imageUri: Uri,
    private val onClick:
        (FilterType) -> Unit
) : RecyclerView.Adapter<
        FilterAdapter.ViewHolder
        >() {

    private val items = FilterType.entries
    private var selected = FilterType.NONE
    fun select(
        filter: FilterType
    ) {
        selected = filter
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemFilterBinding.inflate(
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
        holder.bind(items[position])
    }

    override fun getItemCount() =
        items.size

    inner class ViewHolder(
        private val binding:
        ItemFilterBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            type: FilterType
        ) {
            binding.tvName.text = type.title
            Glide.with(
                binding.image
            )
                .load(imageUri)
                .centerCrop()
                .into(binding.image)
            binding.image.colorFilter =
                if (
                    type == FilterType.NONE
                ) {
                    null
                } else {
                    ImageFilterUtils.create(
                        com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentState(),
                        type
                    )
                }

            val selectedNow = type == selected
            binding.viewSelected.visibility =
                if (selectedNow) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            AnimationUtils.selected(
                binding.root, selectedNow
            )

            binding.root.setOnClickListener {

                AnimationUtils.press(
                    binding.root
                )

                onClick(type)
            }
        }
    }
}