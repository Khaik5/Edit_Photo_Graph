package com.example.drawcanvas_v2.ui.canvas_edit.text

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.databinding.ItemTextColorBinding
import com.example.drawcanvas_v2.data.model.TextColorItem
import com.example.drawcanvas_v2.utils.AnimationUtils

class TextColorAdapter(
    private val items:
    List<TextColorItem>,

    private val onClick:
        (TextColorItem) -> Unit
) : RecyclerView.Adapter<
        TextColorAdapter.ViewHolder
        >() {

    private var selectedPosition =
        0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemTextColorBinding.inflate(
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
            items[position],
            position ==
                    selectedPosition
        )
    }

    override fun getItemCount() =
        items.size

    inner class ViewHolder(
        private val binding:
        ItemTextColorBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: TextColorItem,
            selected: Boolean
        ) {

            binding.viewColor.background =
                GradientDrawable().apply {

                    shape =
                        GradientDrawable.OVAL

                    setColor(
                        item.color
                    )
                }

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

                selectedPosition =
                    position

                notifyDataSetChanged()

                AnimationUtils.press(
                    binding.root
                )

                onClick(item)
            }
        }
    }
}