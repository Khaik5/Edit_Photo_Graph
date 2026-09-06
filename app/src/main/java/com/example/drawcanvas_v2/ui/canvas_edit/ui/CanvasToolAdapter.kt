package com.example.drawcanvas_v2.ui.canvas_edit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.databinding.ItemCanvasToolBinding
import com.example.drawcanvas_v2.utils.AnimationUtils

class CanvasToolAdapter(
    private val tools: List<CanvasTool>,
    private val onClick:
        (CanvasTool) -> Unit
) : RecyclerView.Adapter<CanvasToolAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ItemCanvasToolBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(tools[position])
    }

    override fun getItemCount() = tools.size
    inner class ViewHolder(
        private val binding:
        ItemCanvasToolBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(tool: CanvasTool) {
            binding.ivTool.setImageResource(tool.icon)
            binding.tvTool.text = tool.title
            binding.root.setOnClickListener {
                AnimationUtils.press(binding.root)
                onClick(tool)
            }
        }
    }
}