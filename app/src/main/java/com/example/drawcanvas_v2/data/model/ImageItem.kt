package com.example.drawcanvas_v2.data.model

import android.net.Uri

data class ImageItem(
    val id: Long,
    val image: Uri,
    val favourite: Boolean,
    val selfie: Boolean
)