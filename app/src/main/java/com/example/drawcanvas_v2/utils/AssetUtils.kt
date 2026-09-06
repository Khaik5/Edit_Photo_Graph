package com.example.drawcanvas_v2.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object AssetUtils {

    fun loadBitmap(
        context: Context,
        path: String
    ): Bitmap? {
        return try {
            context.assets
                .open(path)
                .use { stream ->
                    BitmapFactory.decodeStream(
                        stream
                    )
                }
        } catch (
            exception: Exception
        ) {

            null
        }
    }
}