package com.example.drawcanvas_v2.ui.gallery
import com.example.drawcanvas_v2.data.model.GalleryType
sealed interface GalleryAction {

    data class Load(
        val type: GalleryType
    ) : GalleryAction

    data class ImageClick(
        val uri: String
    ) : GalleryAction
}