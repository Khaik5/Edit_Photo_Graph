package com.example.drawcanvas_v2.utils

import android.graphics.RectF
import android.widget.ImageView

object ImageGeometryUtils {

    fun getDisplayRect(
        imageView: ImageView
    ): RectF? {
        val drawable = imageView.drawable ?: return null
        if (
            drawable.intrinsicWidth <= 0 ||  // kích thước thực té của ảnh bằng pixel
            drawable.intrinsicHeight <= 0
        ) {
            return null
        }
        // Tọa độ ảnh góc
        val rect = RectF(
                0f,
                0f,
                drawable.intrinsicWidth.toFloat(),
                drawable.intrinsicHeight.toFloat()
        )

        imageView.imageMatrix.mapRect(
            rect
        )

        return rect
    }
}