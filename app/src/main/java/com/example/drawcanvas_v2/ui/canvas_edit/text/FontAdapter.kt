package com.example.drawcanvas_v2.ui.canvas_edit.text

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.databinding.ItemFontBinding
import com.example.drawcanvas_v2.data.model.FontItem
import com.example.drawcanvas_v2.utils.AnimationUtils

class FontAdapter(
    private val items: List<FontItem>,
    private val onClick: (FontItem) -> Unit
) : RecyclerView.Adapter<FontAdapter.ViewHolder>() {

    private var selectedPosition =
        RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemFontBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
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
            items[position],
            position ==
                    selectedPosition
        )
    }

    override fun getItemCount() =
        items.size

    inner class ViewHolder(
        private val binding:
        ItemFontBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: FontItem,
            selected: Boolean
        ) {

            binding.tvFont.typeface =
                ResourcesCompat.getFont(
                    binding.root.context,
                    item.fontRes
                )

            binding.viewSelected.visibility =
                if (selected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            AnimationUtils.selected(
                binding.root,
                selected
            )

            binding.root.setOnClickListener {

                val position =
                    bindingAdapterPosition

                if (
                    position ==
                    RecyclerView.NO_POSITION
                ) {
                    return@setOnClickListener
                }

                val old =
                    selectedPosition

                selectedPosition =
                    position

                if (
                    old !=
                    RecyclerView.NO_POSITION
                ) {
                    notifyItemChanged(old)
                }

                notifyItemChanged(
                    position
                )

                AnimationUtils.press(
                    binding.root
                )
                onClick(
                    items[position]
                )
            }
        }
    }
}