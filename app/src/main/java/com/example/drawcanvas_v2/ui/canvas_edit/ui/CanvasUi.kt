package com.example.drawcanvas_v2.ui.canvas_edit.ui

import android.view.View
import androidx.core.view.isVisible
import com.example.drawcanvas_v2.databinding.ActivityCanvasEditBinding
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditState
import com.example.drawcanvas_v2.ui.canvas_edit.EditorMode
import com.example.drawcanvas_v2.utils.AnimationUtils

class CanvasUi(
    private val binding: ActivityCanvasEditBinding) {
    private var lastMode: EditorMode? = null
    fun show(
        state: CanvasEditState
    ) {
        binding.stickerView.setInteractionEnabled(state.mode == EditorMode.STICKER || state.mode == EditorMode.TEXT)
        binding.drawView.setInteractionEnabled(state.mode == EditorMode.DRAW)
        if (lastMode == state.mode) {
            return
        }
        lastMode = state.mode
        hidePanels()
        when (state.mode) {
            EditorMode.MAIN -> {
                binding.mainHeader.isVisible = true
                binding.rvTools.isVisible = true
                AnimationUtils.fadeIn(binding.rvTools)
            }
            EditorMode.CROP -> {
                binding.cropContainer.isVisible = true
                showTool(
                    "Crop",
                    binding.panelCrop.root
                )
            }
            EditorMode.STICKER -> {
                showTool("Stickers", binding.panelSticker.root)
            }
            EditorMode.DRAW -> {
                showTool(
                    "Draw",
                    binding.panelDraw.root
                )
            }

            EditorMode.TEXT -> {

                showTool(
                    "Text",
                    binding.panelText.root
                )
            }

            EditorMode.FRAME -> {

                showTool(
                    "Frame",
                    binding.panelFrame.root
                )
            }

            EditorMode.ADJUSTMENT -> {

                showTool(
                    "Adjustment",
                    binding.panelAdjustment.root
                )
            }

            EditorMode.FILTER -> {

                showTool(
                    "Filters",
                    binding.panelFilter.root
                )
            }

        }
    }

    private fun showTool(title: String, panel: View) {
        if(panel.isVisible) return
        binding.toolHeader.isVisible = true
        binding.tvToolTitle.text = title
        binding.btnToolDone.isVisible = true
        binding.btnToolReset.isVisible = true
        panel.visibility = View.VISIBLE
        AnimationUtils.panelIn(panel)
    }

    private fun hidePanels() {
        binding.mainHeader.isVisible = false
        binding.toolHeader.isVisible = false
        binding.rvTools.isVisible = false
        binding.btnToolDone.isVisible = false
        binding.btnToolReset.isVisible = false
        binding.cropContainer.isVisible = false
        binding.panelCrop.root.visibility = View.GONE
        binding.panelSticker.root.visibility = View.GONE
        binding.panelDraw.root.visibility = View.GONE
        binding.panelText.root.visibility = View.GONE
        binding.panelFrame.root.visibility = View.GONE
        binding.panelAdjustment.root.visibility = View.GONE
        binding.panelFilter.root.visibility = View.GONE
    }
}
