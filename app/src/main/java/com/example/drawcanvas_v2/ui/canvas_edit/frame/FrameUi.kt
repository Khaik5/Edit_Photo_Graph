package com.example.drawcanvas_v2.ui.canvas_edit.frame

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawcanvas_v2.databinding.PanelFrameBinding
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditState
import com.example.drawcanvas_v2.utils.AnimationUtils

class FrameUi(
    private val binding: PanelFrameBinding,
    private val onFrameClick:
        (com.example.drawcanvas_v2.data.model.FrameItem) -> Unit
) {

    private val adapter = FrameAdapter(onFrameClick)
    fun setup() {
        binding.rvFrames.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false)
        binding.rvFrames.adapter = adapter
    }
    fun render(
        state: CanvasEditState
    ) {
        adapter.submitList(state.frames)
        if (state.frames.isNotEmpty()) {
            binding.rvFrames.post {
                AnimationUtils.animateItems(binding.rvFrames)
            }
        }
    }
}