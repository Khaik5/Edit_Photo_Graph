package com.example.drawcanvas_v2.ui.canvas_edit.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawcanvas_v2.databinding.ActivityCanvasEditBinding

class MainToolUi(
    private val binding: ActivityCanvasEditBinding,
    private val onToolClick: (CanvasTool) -> Unit
) {
    fun setup() {
        binding.rvTools.layoutManager = LinearLayoutManager(binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.rvTools.adapter = CanvasToolAdapter(CanvasTool.entries, onToolClick)
    }
}