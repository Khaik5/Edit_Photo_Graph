package com.example.drawcanvas_v2.ui.canvas_edit.crop

data class CropState(
    val selectedRatio: CropRatio = CropRatio.FREE,
    val resetVersion: Int = 0
)