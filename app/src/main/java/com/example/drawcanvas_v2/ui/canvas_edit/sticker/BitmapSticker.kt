package com.example.drawcanvas_v2.ui.canvas_edit.sticker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

class BitmapSticker(
    private val bitmap: Bitmap
) : BaseSticker() {

    private val paint =
        Paint(
            Paint.ANTI_ALIAS_FLAG or
                    Paint.FILTER_BITMAP_FLAG
        )

    init {
        updateBounds()
    }

    override fun updateBounds() {
        bounds.set(
            0f,
            0f,
            bitmap.width.toFloat(),
            bitmap.height.toFloat()
        )
    }

    override fun drawContent(
        canvas: Canvas
    ) {
        canvas.drawBitmap(
            bitmap,
            0f,
            0f,
            paint
        )
    }

    fun getBitmap(): Bitmap = bitmap
}