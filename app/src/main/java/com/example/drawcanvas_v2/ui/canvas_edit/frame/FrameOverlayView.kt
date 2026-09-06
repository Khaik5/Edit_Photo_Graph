package com.example.drawcanvas_v2.ui.canvas_edit.frame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class FrameOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var bitmap: Bitmap? = null
    private var targetRect: RectF? = null // vị trí vùng vẽ trên màn hình
    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG) // FILTER_BITMAP_FLAG là lọc ảnh khi scale
    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)
        val frame = bitmap ?: return
        val rect = targetRect ?: RectF(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat()
                ) // vị trí vùng cần vẽ

        canvas.drawBitmap(
            frame,
            null,
            rect,
            paint
        )
    }

    fun setFrame(
        bitmap: Bitmap
    ) {
        this.bitmap = bitmap
        invalidate()
    }
    fun setTargetRect(rect: RectF) { // tạo bản sao của rect
        targetRect = RectF(rect)
        invalidate()
    }
    fun clearFrame() {
        bitmap = null
        targetRect = null
        invalidate()
    }
}