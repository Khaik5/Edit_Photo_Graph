package com.example.drawcanvas_v2.ui.home

sealed interface GalleryHomeEffect {
    data object RequestPermission : GalleryHomeEffect
    data object ShowGallery : GalleryHomeEffect
    data object PermissionDenied : GalleryHomeEffect
}