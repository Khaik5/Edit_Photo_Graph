package com.example.drawcanvas_v2.ui.canvas_edit.draw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.databinding.ItemDrawToolBinding
import com.example.drawcanvas_v2.utils.AnimationUtils

class DrawToolAdapter(
    private val onClick: (DrawTool) -> Unit
) : RecyclerView.Adapter<DrawToolAdapter.ViewHolder>() {

    private val items =
        DrawTool.entries

    private var selected:
            DrawTool? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemDrawToolBinding.inflate(
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

    override fun getItemCount(): Int =
        items.size

    fun setSelected(
        tool: DrawTool?
    ) {

        selected = tool

        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding:
        ItemDrawToolBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            tool: DrawTool
        ) {

            val isSelected =
                tool == selected

            binding.ivTool.setImageResource(
                tool.icon
            )

            binding.tvTitle.text =
                tool.title

            binding.viewSelected.visibility =
                if (isSelected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            AnimationUtils.selected(
                binding.root,
                isSelected
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

                selected =
                    tool

                notifyDataSetChanged()

                AnimationUtils.press(
                    binding.root
                )

                onClick(tool)
            }
        }
    }
}