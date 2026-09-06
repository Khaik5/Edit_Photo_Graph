package com.example.drawcanvas_v2.ui.canvas_edit.filter

sealed interface FilterAction {

    data class Select(
        val filter: FilterType
    ) : FilterAction

    data object Apply : FilterAction

    data object Cancel : FilterAction
    data object Reset : FilterAction
    data object ResetAll : FilterAction
}