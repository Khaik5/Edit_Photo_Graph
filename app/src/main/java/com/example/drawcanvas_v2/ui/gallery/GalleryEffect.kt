package com.example.drawcanvas_v2.ui.gallery

sealed interface GalleryEffect {

    data class OpenPreview(
        val uri: String
    ) : GalleryEffect

    data class ShowMessage(
        val message: String
    ) : GalleryEffect
}