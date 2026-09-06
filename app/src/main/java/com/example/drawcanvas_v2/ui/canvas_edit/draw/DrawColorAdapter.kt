package com.example.drawcanvas_v2.ui.canvas_edit.draw

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.databinding.ItemDrawColorBinding
import com.example.drawcanvas_v2.data.model.DrawColorItem
import com.example.drawcanvas_v2.utils.AnimationUtils



class DrawColorAdapter(
    private val items: List<DrawColorItem>,
    private val onClick: (DrawColorItem) -> Unit
) : RecyclerView.Adapter<DrawColorAdapter.ViewHolder>() {

    private var selectedPosition =
        RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemDrawColorBinding.inflate(
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
            position == selectedPosition
        )
    }

    override fun getItemCount(): Int =
        items.size

    inner class ViewHolder(
        private val binding:
        ItemDrawColorBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: DrawColorItem,
            selected: Boolean
        ) {

            binding.viewColor.setBackgroundColor(
                item.color
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

                notifyItemChanged(position)

                AnimationUtils.press(
                    binding.root
                )

                onClick(item)
            }
        }
    }
}