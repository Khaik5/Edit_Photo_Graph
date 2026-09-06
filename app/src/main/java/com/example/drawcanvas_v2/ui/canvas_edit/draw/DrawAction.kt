package com.example.drawcanvas_v2.ui.canvas_edit.draw

sealed interface DrawAction {

    data class ToolClick(
        val tool: DrawTool
    ) : DrawAction

    data class ColorClick(
        val color: Int
    ) : DrawAction

    data class SizeChanged(
        val size: Int
    ) : DrawAction
}