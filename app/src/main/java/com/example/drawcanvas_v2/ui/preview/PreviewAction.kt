package com.example.drawcanvas_v2.ui.preview

sealed interface PreviewAction {

    data class Load(
        val imageUri: String?
    ) : PreviewAction

    data object Back :
        PreviewAction

    data object Edit :
        PreviewAction
}