package com.example.drawcanvas_v2.ui.preview

sealed interface PreviewEffect {
    data object Finish :
        PreviewEffect
    data class OpenEditor(
        val imageUri: String
    ) : PreviewEffect
}