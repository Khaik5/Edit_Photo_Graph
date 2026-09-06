package com.example.drawcanvas_v2.utils

import com.example.drawcanvas_v2.databinding.ActivityCanvasEditBinding

object CanvasResetUtils {
    fun reset(binding: ActivityCanvasEditBinding) {
        binding.ivImage.clearColorFilter()
        binding.drawView.reset()
        binding.stickerView.clearStickers()
        binding.frameOverlay.clearFrame()
    }
}
