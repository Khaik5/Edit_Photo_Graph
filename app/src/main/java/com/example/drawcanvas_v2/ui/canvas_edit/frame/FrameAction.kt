package com.example.drawcanvas_v2.ui.canvas_edit.frame
sealed interface FrameAction {
    data object Load : FrameAction
    data class Select(
        val item: com.example.drawcanvas_v2.data.model.FrameItem
    ) : FrameAction
    data object Reset : FrameAction
}