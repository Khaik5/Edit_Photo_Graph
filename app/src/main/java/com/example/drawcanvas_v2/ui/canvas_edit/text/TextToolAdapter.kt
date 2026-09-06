package com.example.drawcanvas_v2.ui.canvas_edit.text

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.databinding.ItemTextToolBinding
import com.example.drawcanvas_v2.utils.AnimationUtils

class TextToolAdapter(
    private val tools:
    List<TextTool>,

    private val onClick:
        (TextTool) -> Unit
) : RecyclerView.Adapter<
        TextToolAdapter.ViewHolder
        >() {

    private var selectedPosition =
        0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemTextToolBinding.inflate(
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
            tools[position],
            position ==
                    selectedPosition
        )
    }

    override fun getItemCount() =
        tools.size

    inner class ViewHolder(
        private val binding:
        ItemTextToolBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            tool: TextTool,
            selected: Boolean
        ) {

            binding.iconFont.visibility =
                View.GONE

            binding.iconColor.visibility =
                View.GONE

            binding.iconStroke.visibility =
                View.GONE

            binding.iconSize.visibility =
                View.GONE

            when (tool) {

                TextTool.FONT -> {

                    binding.tvTitle.text =
                        "Font"

                    binding.iconFont.visibility =
                        View.VISIBLE
                }

                TextTool.COLOR -> {

                    binding.tvTitle.text =
                        "Color"

                    binding.iconColor.visibility =
                        View.VISIBLE
                }

                TextTool.STROKE -> {

                    binding.tvTitle.text =
                        "Stroke"

                    binding.iconStroke.visibility =
                        View.VISIBLE
                }

                TextTool.SIZE -> {

                    binding.tvTitle.text =
                        "Text Size"

                    binding.iconSize.visibility =
                        View.VISIBLE
                }
            }

            binding.selection.visibility =
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

                onClick(
                    tool
                )
            }
        }
    }
}