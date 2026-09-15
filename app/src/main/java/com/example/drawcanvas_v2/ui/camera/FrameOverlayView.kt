package com.example.drawcanvas_v2.ui.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class FrameOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val gridPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(190, 255, 255, 255)
            strokeWidth = resources.displayMetrics.density
        }
    private val shadePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(150, 0, 0, 0)
            style = Paint.Style.FILL
        }
    private var gridState = true
    private var cameraSize = CameraSize.S4_3

    fun setGrid(state: Boolean) {
        gridState = state
        invalidate()
    }

    fun setCameraSize(size: CameraSize) {
        cameraSize = size
        invalidate()
    }

    fun setCountdown(value: Int?) {
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val frameRect =
            getFrameRect()

        if (cameraSize != CameraSize.SFull) {
            drawShade(canvas, frameRect)
        }

        if (gridState) {
            drawGrid(canvas, frameRect)
        }

    }

    private fun drawGrid(
        canvas: Canvas,
        rect: RectF
    ) {
        val oneThirdX =
            rect.left + rect.width() / 3f
        val twoThirdX =
            rect.left + rect.width() * 2f / 3f
        val oneThirdY =
            rect.top + rect.height() / 3f
        val twoThirdY =
            rect.top + rect.height() * 2f / 3f
        canvas.drawLine(oneThirdX, rect.top, oneThirdX, rect.bottom, gridPaint)
        canvas.drawLine(twoThirdX, rect.top, twoThirdX, rect.bottom, gridPaint)
        canvas.drawLine(rect.left, oneThirdY, rect.right, oneThirdY, gridPaint)
        canvas.drawLine(rect.left, twoThirdY, rect.right, twoThirdY, gridPaint)
    }

    private fun drawShade(
        canvas: Canvas,
        rect: RectF
    ) {
        canvas.drawRect(0f, 0f, width.toFloat(), rect.top, shadePaint)
        canvas.drawRect(0f, rect.bottom, width.toFloat(), height.toFloat(), shadePaint)
        canvas.drawRect(0f, rect.top, rect.left, rect.bottom, shadePaint)
        canvas.drawRect(rect.right, rect.top, width.toFloat(), rect.bottom, shadePaint)
    }

    private fun getFrameRect(): RectF {
        val viewWidth =
            width.toFloat()
        val viewHeight =
            height.toFloat()
        val landscapeRatio =
            when (cameraSize) {
                CameraSize.S1_1 -> 1f
                CameraSize.S4_3 -> 4f / 3f
                CameraSize.S16_9 -> 16f / 9f
                CameraSize.SFull -> return RectF(0f, 0f, viewWidth, viewHeight)
            }

        // Keep the guide's crop window identical to the crop performed on the
        // captured bitmap, including landscape and portrait orientations.
        val targetRatio =
            if (viewHeight > viewWidth) 1f / landscapeRatio else landscapeRatio
        val frameWidth = minOf(viewWidth, viewHeight * targetRatio)
        val frameHeight = frameWidth / targetRatio
        val left = (viewWidth - frameWidth) / 2f
        val top = (viewHeight - frameHeight) / 2f
        return RectF(
            left,
            top,
            left + frameWidth,
            top + frameHeight
        )
    }

}
