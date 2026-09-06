package com.example.drawcanvas_v2.ui.home
sealed interface GalleryHomeAction {

    data class Start(
        val hasPermission: Boolean
    ) : GalleryHomeAction
    data class PermissionResult(
        val granted: Boolean
    ) : GalleryHomeAction
    data class TabChanged(
        val position: Int
    ) : GalleryHomeAction
}