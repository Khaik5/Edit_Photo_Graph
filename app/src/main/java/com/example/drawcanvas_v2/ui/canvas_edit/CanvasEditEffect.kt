package com.example.drawcanvas_v2.ui.canvas_edit

sealed interface CanvasEditEffect {

    data object SaveImage : CanvasEditEffect

    data object RestoreImage : CanvasEditEffect

    data object ConfirmRestore : CanvasEditEffect

    data object ConfirmExit : CanvasEditEffect

    data object Finish : CanvasEditEffect

    data class ShowMessage(
        val message: String
    ) : CanvasEditEffect
}