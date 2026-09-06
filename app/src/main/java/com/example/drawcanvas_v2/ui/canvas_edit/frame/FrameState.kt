package com.example.drawcanvas_v2.ui.canvas_edit.frame

import com.example.drawcanvas_v2.data.model.FrameItem

data class FrameState(
    val loading: Boolean = false,
    val frames: List<FrameItem> = emptyList()
)