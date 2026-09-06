package com.example.drawcanvas_v2.ui.canvas_edit.adjustment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.databinding.ItemAdjustmentBinding
import com.example.drawcanvas_v2.utils.AnimationUtils

class AdjustmentAdapter(
    private val onClick: (AdjustmentType) -> Unit
) : RecyclerView.Adapter<AdjustmentAdapter.ViewHolder>() {
    private val items = AdjustmentType.entries // list mục item có bao item

    private var selected: AdjustmentType? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemAdjustmentBinding.inflate(
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

    override fun getItemCount() = items.size

    fun setSelected(
        type: AdjustmentType?
    ) {
        selected = type
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding:
        ItemAdjustmentBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: AdjustmentType
        ) {

            val isSelected = item == selected

            binding.ivIcon.setImageResource(item.icon)
            binding.tvName.text = item.title
            binding.viewSelected.visibility =
                if (isSelected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            AnimationUtils.selected(
                binding.root, isSelected
            )

            binding.root.setOnClickListener {

                val position = bindingAdapterPosition // vị trí hiện tại

                if (position == RecyclerView.NO_POSITION) { // vị trí -1
                    return@setOnClickListener
                }

                selected = item
                notifyDataSetChanged()
                AnimationUtils.press(binding.root)
                onClick(item)
            }
        }
    }
}