package com.example.drawcanvas_v2.ui.canvas_edit.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.example.drawcanvas_v2.ui.canvas_edit.sticker.BaseSticker

class TextSticker(
    private val context: Context,
    text: String
) : BaseSticker() {
    private var text = text
    private var textColor = Color.WHITE
    private var strokeColor = Color.BLACK
    private var textSize = 36f
    private var strokeWidth = 0f
    private var typeface: Typeface? = null
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.LEFT
            style = Paint.Style.FILL
        }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.LEFT
            style = Paint.Style.STROKE
        }

    init {
        updatePaint()
        updateBounds()
    }
    override fun drawContent(
        canvas: Canvas
    ) {

        val baseline =
            -fillPaint.ascent()

        if (strokeWidth > 0f) {
            canvas.drawText(
                text,
                0f,
                baseline,
                strokePaint
            )
        }

        canvas.drawText(
            text,
            0f,
            baseline,
            fillPaint
        )
    }

    override fun updateBounds() {

        updatePaint()

        val width =
            fillPaint.measureText(
                text
            )

        val height =
            fillPaint.descent() -
                    fillPaint.ascent()

        val padding =
            20f + strokeWidth

        bounds.set(
            -padding,
            -padding,
            width + padding,
            height + padding
        )
    }

    private fun updatePaint() {

        fillPaint.apply {
            color = textColor
            textSize =
                this@TextSticker.textSize
            typeface =
                this@TextSticker.typeface
        }

        strokePaint.apply {
            color = strokeColor
            textSize =
                this@TextSticker.textSize
            typeface =
                this@TextSticker.typeface
            strokeWidth =
                this@TextSticker.strokeWidth
            strokeJoin =
                Paint.Join.ROUND
            strokeCap =
                Paint.Cap.ROUND
        }
    }

    fun setText(
        value: String
    ) {
        text = value
        updateBounds()
    }

    fun getText(): String =
        text

    fun setTextColor(
        color: Int
    ) {
        textColor = color
        updatePaint()
    }

    fun setStrokeColor(
        color: Int
    ) {
        strokeColor = color
        updatePaint()
    }

    fun setStrokeWidth(
        width: Float
    ) {
        strokeWidth =
            width.coerceIn(0f, 20f)
        updateBounds()
    }

    fun setTextSize(
        size: Float
    ) {
        textSize =
            size.coerceIn(10f, 100f)
        updateBounds()
    }

    fun clearFont() {
        typeface = null
        updateBounds()
    }

    fun setFont(
        @FontRes fontRes: Int
    ) {
        typeface =
            ResourcesCompat.getFont(
                context,
                fontRes
            )
        updateBounds()
    }

    fun getTextSize(): Float =
        textSize

    fun getStrokeWidth(): Float =
        strokeWidth
}