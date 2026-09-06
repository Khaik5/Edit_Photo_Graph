package com.example.drawcanvas_v2.ui.canvas_edit.adjustment

sealed interface AdjustmentAction {

    data class Select(
        val type: AdjustmentType
    ) : AdjustmentAction

    data class ValueChanged(
        val value: Int
    ) : AdjustmentAction

    data object Reset :
        AdjustmentAction

    data object Apply :
        AdjustmentAction

    data object Cancel :
        AdjustmentAction

    data object ResetAll :
        AdjustmentAction
}