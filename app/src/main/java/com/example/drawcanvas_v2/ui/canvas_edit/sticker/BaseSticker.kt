package com.example.drawcanvas_v2.ui.canvas_edit.sticker

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF

abstract class BaseSticker {

    val matrix =
        Matrix()

    protected val bounds =
        RectF()

    var isSelected =
        false

    abstract fun drawContent(
        canvas: Canvas
    )

    abstract fun updateBounds()

    fun draw(
        canvas: Canvas
    ) {

        canvas.save()

        canvas.concat(
            matrix
        )

        drawContent(
            canvas
        )

        canvas.restore()
    }

    fun contains(
        x: Float,
        y: Float
    ): Boolean {

        val inverse =
            Matrix()

        if (!matrix.invert(inverse)) {
            return false
        }

        val point =
            floatArrayOf(
                x,
                y
            )

        inverse.mapPoints(
            point
        )

        return bounds.contains(
            point[0],
            point[1]
        )
    }

    fun moveCenterTo(
        centerX: Float,
        centerY: Float
    ) {

        updateBounds()

        matrix.reset()

        val stickerCenterX =
            bounds.centerX()

        val stickerCenterY =
            bounds.centerY()

        matrix.postTranslate(
            centerX - stickerCenterX,
            centerY - stickerCenterY
        )
    }

    fun getScreenCorners():
            FloatArray {

        updateBounds()

        val points =
            floatArrayOf(
                bounds.left,
                bounds.top,

                bounds.right,
                bounds.top,

                bounds.right,
                bounds.bottom,

                bounds.left,
                bounds.bottom
            )

        matrix.mapPoints(
            points
        )

        return points
    }

    fun getScreenCenter():
            PointF {

        updateBounds()

        val point =
            floatArrayOf(
                bounds.centerX(),
                bounds.centerY()
            )

        matrix.mapPoints(
            point
        )

        return PointF(
            point[0],
            point[1]
        )
    }
}