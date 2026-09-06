package com.example.drawcanvas_v2.ui.canvas_edit.adjustment

import androidx.annotation.DrawableRes
import com.example.drawcanvas_v2.R

enum class AdjustmentType(
    val title: String,
    @DrawableRes
    val icon: Int
) {

    BRIGHTNESS(
        title = "Brightness",
        icon = R.drawable.ic_bright
    ),

    CONTRAST(
        title = "Contrast",
        icon = R.drawable.ic_contrast
    ),

    SATURATION(
        title = "Saturation",
        icon = R.drawable.ic_saturation
    ),

    HUE(
        title = "Hue",
        icon = R.drawable.ic_hue
    ),
}