package com.example.drawcanvas_v2.ui.canvas_edit.sticker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.drawcanvas_v2.R
import kotlin.math.atan2
import kotlin.math.sqrt

class StickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var touchEnabled = true

    private val stickers =
        mutableListOf<BaseSticker>()

    private var selectedSticker:
            BaseSticker? = null

    private enum class Mode {
        NONE,
        DRAG,
        SCALE_ROTATE
    }

    private var mode =
        Mode.NONE

    private var lastX = 0f
    private var lastY = 0f

    private val pivot =
        PointF()

    private var startDistance =
        0f

    private var startAngle =
        0f

    private var lastTapTime =
        0L

    private var lastTapSticker:
            BaseSticker? = null

    var onStickerSelected:
            ((BaseSticker?) -> Unit)? = null

    var onStickerDoubleClick:
            ((BaseSticker) -> Unit)? = null

    private val borderPaint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.WHITE

            style =
                Paint.Style.STROKE

            strokeWidth =
                3f

            pathEffect =
                DashPathEffect(
                    floatArrayOf(
                        12f,
                        8f
                    ),
                    0f
                )
        }

    private val handlePaint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.WHITE

            style =
                Paint.Style.FILL
        }

    private val deletePaint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.BLACK

            style =
                Paint.Style.STROKE

            strokeWidth =
                4f

            strokeCap =
                Paint.Cap.ROUND
        }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        stickers.forEach {
            it.draw(canvas)
        }

        selectedSticker?.let {

            if (it.isSelected) {
                drawSelection(
                    canvas,
                    it
                )
            }
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        if (!touchEnabled) {
            return false
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                handleDown(event)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                handleMove(event)
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                mode = Mode.NONE
                return true
            }
        }
        return true
    }

    private fun handleDown(
        event: MotionEvent
    ) {
        val x = event.x
        val y = event.y
        val current = selectedSticker
        if (
            current != null &&
            current.isSelected
        ) {
            val corners =
                current.getScreenCorners()
            // Delete handle
            val deleteX = corners[0]
            val deleteY = corners[1]
            if (
                distance(
                    deleteX,
                    deleteY,
                    x,
                    y
                ) < 55f
            ) {
                stickers.remove(current)
                current.isSelected = false
                selectedSticker = null
                onStickerSelected?.invoke(null
                )
                invalidate()
                return
            }
            // Scale / rotate handle
            val scaleX = corners[4]
            val scaleY = corners[5]
            if (
                distance(
                    scaleX,
                    scaleY,
                    x,
                    y
                ) < 55f
            ) {
                val center =
                    current.getScreenCenter()

                pivot.set(
                    center.x,
                    center.y
                )

                startDistance =
                    distance(
                        center.x,
                        center.y,
                        x,
                        y
                    )

                startAngle =
                    angle(
                        center.x,
                        center.y,
                        x,
                        y
                    )

                mode =
                    Mode.SCALE_ROTATE

                return
            }
        }

        val touched =
            stickers
                .asReversed()
                .firstOrNull {
                    it.contains(
                        x,
                        y
                    )
                }

        stickers.forEach {
            it.isSelected =
                false
        }

        if (touched == null) {

            selectedSticker =
                null

            onStickerSelected?.invoke(
                null
            )

            invalidate()

            return
        }

        touched.isSelected =
            true

        selectedSticker =
            touched

        stickers.remove(
            touched
        )
        stickers.add(touched)
        lastX = x
        lastY = y
        mode = Mode.DRAG
        val now = System.currentTimeMillis()

        if (
            lastTapSticker === touched &&
            now - lastTapTime < 300L
        ) {

            onStickerDoubleClick?.invoke(
                touched
            )

            lastTapSticker =
                null

            lastTapTime =
                0L

        } else {

            lastTapSticker =
                touched

            lastTapTime =
                now
        }

        onStickerSelected?.invoke(
            touched
        )

        invalidate()
    }

    private fun handleMove(
        event: MotionEvent
    ) {

        val current =
            selectedSticker
                ?: return

        when (mode) {

            Mode.DRAG -> {

                current.matrix.postTranslate(
                    event.x - lastX,
                    event.y - lastY
                )

                lastX =
                    event.x

                lastY =
                    event.y

                invalidate()
            }

            Mode.SCALE_ROTATE -> {

                val newDistance =
                    distance(
                        pivot.x,
                        pivot.y,
                        event.x,
                        event.y
                    )

                if (
                    startDistance > 0f
                ) {

                    val scale =
                        newDistance /
                                startDistance

                    if (
                        scale in 0.15f..8f
                    ) {

                        current.matrix.postScale(
                            scale,
                            scale,
                            pivot.x,
                            pivot.y
                        )

                        startDistance =
                            newDistance
                    }
                }
                val newAngle =
                    angle(
                        pivot.x,
                        pivot.y,
                        event.x,
                        event.y
                    )

                current.matrix.postRotate(
                    newAngle - startAngle,
                    pivot.x,
                    pivot.y
                )
                startAngle = newAngle
                invalidate()
            }
            Mode.NONE -> Unit
        }
    }

    private fun drawSelection(
        canvas: Canvas,
        sticker: BaseSticker
    ) {

        val corners =
            sticker.getScreenCorners()

        val path =
            Path()

        path.moveTo(
            corners[0],
            corners[1]
        )

        path.lineTo(
            corners[2],
            corners[3]
        )

        path.lineTo(
            corners[4],
            corners[5]
        )

        path.lineTo(
            corners[6],
            corners[7]
        )

        path.close()

        canvas.drawPath(
            path,
            borderPaint
        )

        drawHandle(
            canvas,
            corners[0],
            corners[1],
            true
        )

        drawHandle(
            canvas,
            corners[4],
            corners[5],
            false
        )
    }

    private fun drawHandle(
        canvas: Canvas,
        x: Float,
        y: Float,
        delete: Boolean
    ) {

        canvas.drawCircle(
            x,
            y,
            24f,
            handlePaint
        )

        if (delete) {
            canvas.drawLine(
                x - 8f,
                y - 8f,
                x + 8f,
                y + 8f,
                deletePaint
            )

            canvas.drawLine(
                x + 8f,
                y - 8f,
                x - 8f,
                y + 8f,
                deletePaint
            )
        } else {
            val icon = ContextCompat.getDrawable(context, R.drawable.ic_scale)
            icon?.let {
                val size = 30f
                val left = (x - size / 2).toInt()
                val top = (y - size / 2).toInt()
                it.setBounds(left, top, left + size.toInt(), top + size.toInt())
                it.draw(canvas)
            }
        }
    }

    fun setInteractionEnabled(
        enabled: Boolean
    ) {

        touchEnabled =
            enabled
        if (!enabled) {
            clearSelection()
        }

        invalidate()
    }

    fun addSticker(
        sticker: BaseSticker
    ) {

        post {

            stickers.forEach {
                it.isSelected =
                    false
            }

            sticker.moveCenterTo(
                width / 2f,
                height / 2f
            )

            sticker.isSelected =
                true

            stickers.add(
                sticker
            )

            selectedSticker =
                sticker

            onStickerSelected?.invoke(
                sticker
            )

            invalidate()
        }
    }

    fun getSelectedSticker():
            BaseSticker? =
        selectedSticker

    fun clearSelection() {

        stickers.forEach {
            it.isSelected =
                false
        }

        selectedSticker =
            null

        onStickerSelected?.invoke(
            null
        )

        invalidate()
    }

    fun clearStickers() {

        stickers.clear()

        selectedSticker =
            null

        onStickerSelected?.invoke(
            null
        )

        invalidate()
    }

    private fun distance(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Float {

        return sqrt(
            ((x2 - x1) *
                    (x2 - x1)) +
                    ((y2 - y1) *
                            (y2 - y1))
        )
    }

    private fun angle(
        cx: Float,
        cy: Float,
        x: Float,
        y: Float
    ): Float {

        return Math.toDegrees(
            atan2(
                (y - cy).toDouble(),
                (x - cx).toDouble()
            )
        ).toFloat()
    }
}