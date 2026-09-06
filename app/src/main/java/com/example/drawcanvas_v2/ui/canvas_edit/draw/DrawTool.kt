package com.example.drawcanvas_v2.ui.canvas_edit.draw

import androidx.annotation.DrawableRes
import com.example.drawcanvas_v2.R

enum class DrawTool(
    val title: String,
    @DrawableRes val icon: Int
) {

    COLOR(
        title = "Color",
        icon = R.drawable.ic_draw
    ),

    ERASER(
        title = "Eraser",
        icon = R.drawable.ic_eraser
    ),

    PAINT_SIZE(
        title = "Size",
        icon = R.drawable.ic_paint_size
    )
}