package com.example.drawcanvas_v2.ui.gallery
import com.example.drawcanvas_v2.data.model.ImageItem
data class GalleryState(
    val isLoading: Boolean =
        false,

    val images: List<ImageItem> =
        emptyList()
)