package com.example.drawcanvas_v2.ui.canvas_edit.draw

import android.graphics.Color

data class DrawState(
    val tool: DrawTool = DrawTool.COLOR,
    val color: Int = Color.WHITE,
    val size:
    Int = 14
)