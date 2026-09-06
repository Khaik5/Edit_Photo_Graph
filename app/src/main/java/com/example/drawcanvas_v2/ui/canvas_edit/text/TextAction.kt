package com.example.drawcanvas_v2.ui.canvas_edit.text

import com.example.drawcanvas_v2.data.model.FontItem

sealed interface TextAction {

    data class ToolClick(
        val tool: TextTool
    ) : TextAction

    data class FontSelected(
        val font: FontItem
    ) : TextAction

    data class ColorSelected(
        val color: Int
    ) : TextAction

    data class SizeChanged(
        val size: Int
    ) : TextAction

    data class StrokeChanged(
        val stroke: Int
    ) : TextAction

    data object EditSelected : TextAction
}