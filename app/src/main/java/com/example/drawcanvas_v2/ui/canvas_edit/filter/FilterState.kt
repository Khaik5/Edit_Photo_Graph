package com.example.drawcanvas_v2.ui.canvas_edit.filter

data class FilterState(
    val selected: FilterType = FilterType.NONE,
    val dirty: Boolean = false
)