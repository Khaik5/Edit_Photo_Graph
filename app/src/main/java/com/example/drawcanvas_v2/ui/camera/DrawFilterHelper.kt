package com.example.drawcanvas_v2.ui.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark

object DrawFilterHelper {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    fun getMatrix(
        sourceWidth: Int,
        sourceHeight: Int,
        viewWidth: Int,
        viewHeight: Int,
        isFrontCamera: Boolean
    ): Matrix {
        val scale = minOf(
            viewWidth / sourceWidth.toFloat(),
            viewHeight / sourceHeight.toFloat()
        )
        //đưa ảnh vaào giữa view
        val dx = (viewWidth - sourceWidth * scale) / 2f
        val dy = (viewHeight - sourceHeight * scale) / 2f

        return Matrix().apply {
            if (isFrontCamera) {
                postScale(-1f, 1f)
                postTranslate(sourceWidth.toFloat(), 0f)
            }
            postScale(scale, scale)
            postTranslate(dx, dy)
        }
    }

    fun drawHeadFilter(
        canvas: Canvas,
        face: Face,
        filterBitmap: Bitmap,
        matrix: Matrix = Matrix()
    ) {
        val rect = RectF(face.boundingBox)
        matrix.mapRect(rect)
        val targetWidth = rect.width() * 1.35f
        val targetHeight = targetWidth * filterBitmap.height / filterBitmap.width
        val target =
            RectF(
                rect.centerX() - targetWidth / 2f,
                rect.top - targetHeight * 0.62f,
                rect.centerX() + targetWidth / 2f,
                rect.top + targetHeight * 0.28f
            )

        canvas.drawBitmap(
            filterBitmap,
            null,
            target,
            paint
        )
    }

    fun drawCheekFilter(
        canvas: Canvas,
        face: Face,
        filterBitmap: Bitmap,
        matrix: Matrix = Matrix()
    ) {
        val left =
            face.getLandmark(FaceLandmark.LEFT_CHEEK)?.position
        val right =
            face.getLandmark(FaceLandmark.RIGHT_CHEEK)?.position

        if (left == null || right == null) {
            drawCheekFilterResult(
                canvas,
                face,
                filterBitmap,
                matrix
            )
            return
        }

        val points =
            floatArrayOf(
                left.x,
                left.y,
                right.x,
                right.y
            )
        matrix.mapPoints(points)

        val faceRect =
            RectF(face.boundingBox)
        matrix.mapRect(faceRect)
        val cheekSize =
            faceRect.width() * 0.22f

        drawCentered(
            canvas,
            filterBitmap,
            points[0],
            points[1],
            cheekSize
        )
        drawCentered(
            canvas,
            filterBitmap,
            points[2],
            points[3],
            cheekSize
        )
    }

    fun drawCheekFilterResult(
        canvas: Canvas,
        face: Face,
        filterBitmap: Bitmap,
        matrix: Matrix = Matrix()
    ) {
        val rect =
            RectF(face.boundingBox)
        matrix.mapRect(rect)
        val cheekSize =
            rect.width() * 0.22f
        val y =
            rect.top + rect.height() * 0.62f

        drawCentered(
            canvas,
            filterBitmap,
            rect.left + rect.width() * 0.30f,
            y,
            cheekSize
        )
        drawCentered(
            canvas,
            filterBitmap,
            rect.left + rect.width() * 0.70f,
            y,
            cheekSize
        )
    }

    private fun drawCentered(
        canvas: Canvas,
        bitmap: Bitmap,
        centerX: Float,
        centerY: Float,
        size: Float
    ) {
        val target =
            RectF(
                centerX - size / 2f,
                centerY - size / 2f,
                centerX + size / 2f,
                centerY + size / 2f
            )
        canvas.drawBitmap(
            bitmap,
            null,
            target,
            paint
        )
    }
}
