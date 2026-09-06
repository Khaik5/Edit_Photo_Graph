package com.example.drawcanvas_v2.ui.canvas_edit.text

sealed interface TextEffect {

    data class EditText(
        val text: String
    ) : TextEffect
}