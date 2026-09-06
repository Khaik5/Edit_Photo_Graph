package com.example.drawcanvas_v2.ui.canvas_edit.crop

sealed interface CropAction {
    data class SelectRatio(val ratio: CropRatio) : CropAction
    data object Reset : CropAction
}