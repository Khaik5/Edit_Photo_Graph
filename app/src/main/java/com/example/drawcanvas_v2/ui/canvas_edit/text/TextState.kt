package com.example.drawcanvas_v2.ui.canvas_edit.text

import com.example.drawcanvas_v2.data.model.FontItem

data class TextState(
    val tool: TextTool = TextTool.FONT,
    val selectedFont:
    FontItem? = null,
    val selectedColor:
    Int = android.graphics.Color.WHITE,

    val size: Int = 36,

    val stroke: Int = 0
)