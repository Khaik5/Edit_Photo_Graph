package com.example.drawcanvas_v2.ui.canvas_edit.adjustment

data class AdjustmentState(
    val selected: AdjustmentType = AdjustmentType.BRIGHTNESS,
    val brightness: Int = 0,
    val contrast: Int = 0,
    val saturation: Int = 0,
    val hue: Int = 0
)