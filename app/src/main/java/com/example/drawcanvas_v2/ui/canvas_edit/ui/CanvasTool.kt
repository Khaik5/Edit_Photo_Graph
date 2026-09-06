package com.example.drawcanvas_v2.ui.canvas_edit.ui

import androidx.annotation.DrawableRes
import com.example.drawcanvas_v2.R

enum class CanvasTool(
    val title: String,
    @DrawableRes val icon: Int
) {

    CROP(
        "Crop",
        R.drawable.ic_crop
    ),

    STICKER(
        "Sticker",
        R.drawable.ic_sticker
    ),

    ADJUSTMENT(
        "Adjust",
        R.drawable.ic_adjustment
    ),

    DRAW(
        "Draw",
        R.drawable.ic_draw
    ),

    TEXT(
        "Text",
        R.drawable.ic_text
    ),

    FRAME(
        "Frame",
        R.drawable.ic_frame
    ),

    FILTER(
        "Filter",
        R.drawable.ic_filter
    )
}