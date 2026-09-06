package com.example.drawcanvas_v2.ui.canvas_edit.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

private data class DrawPath(
    val path: Path,
    val color: Int,
    val size: Float,
    val eraser: Boolean
)

class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paths = mutableListOf<DrawPath>()

    private val redoPaths = mutableListOf<DrawPath>()

    private var currentPath:
            Path? = null

    private var currentColor =
        Color.WHITE

    private var currentSize =
        14f

    private var eraserSize =
        14f

    private var isEraser =
        false

    private var touchEnabled = false

    var onDrawingChanged:
            (() -> Unit)? = null

    private val paint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            style =
                Paint.Style.STROKE

            strokeCap =
                Paint.Cap.ROUND

            strokeJoin =
                Paint.Join.ROUND
        }

    init {

        setLayerType(
            LAYER_TYPE_SOFTWARE,
            null
        )
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        paths.forEach {
            drawItem(
                canvas,
                it
            )
        }

        currentPath?.let {

            drawItem(
                canvas,
                DrawPath(
                    path = it,
                    color = currentColor,
                    size =
                        if (isEraser) {
                            eraserSize
                        } else {
                            currentSize
                        },
                    eraser = isEraser
                )
            )
        }
    }

    private fun drawItem(
        canvas: Canvas,
        item: DrawPath
    ) {

        paint.strokeWidth =
            item.size

        if (item.eraser) {

            paint.color =
                Color.TRANSPARENT

            paint.xfermode =
                PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR
                )

        } else {

            paint.color =
                item.color

            paint.xfermode =
                null
        }

        canvas.drawPath(
            item.path,
            paint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        if (!touchEnabled) {
            return false
        }

        when (
            event.actionMasked
        ) {

            MotionEvent.ACTION_DOWN -> {

                redoPaths.clear()

                currentPath =
                    Path().apply {

                        moveTo(
                            event.x,
                            event.y
                        )
                    }

                invalidate()

                return true
            }

            MotionEvent.ACTION_MOVE -> {

                currentPath?.lineTo(
                    event.x,
                    event.y
                )

                invalidate()

                return true
            }

            MotionEvent.ACTION_UP -> {

                currentPath?.let {

                    paths.add(
                        DrawPath(
                            path = Path(it),
                            color = currentColor,
                            size =
                                if (isEraser) {
                                    eraserSize
                                } else {
                                    currentSize
                                },
                            eraser = isEraser
                        )
                    )
                }

                currentPath = null

                invalidate()

                onDrawingChanged?.invoke()

                return true
            }

            MotionEvent.ACTION_CANCEL -> {

                currentPath = null

                invalidate()

                return true
            }
        }

        return true
    }

    fun setInteractionEnabled(enabled: Boolean) {
        touchEnabled = enabled
        if (!enabled) {
            currentPath = null
            invalidate()
        }
    }

    fun usePen() {
        isEraser = false
    }

    fun useEraser() {
        isEraser = true
    }

    fun setPenColor(
        color: Int
    ) {

        currentColor = color
        isEraser = false
    }

    fun setPenSize(
        size: Float
    ) {

        currentSize =
            size.coerceAtLeast(1f)
    }

    fun setEraserSize(
        size: Float
    ) {

        eraserSize =
            size.coerceAtLeast(1f)
    }

    fun undo() {

        if (paths.isEmpty()) {
            return
        }

        redoPaths.add(
            paths.removeAt(
                paths.lastIndex
            )
        )

        invalidate()

        onDrawingChanged?.invoke()
    }

    fun redo() {

        if (redoPaths.isEmpty()) {
            return
        }

        paths.add(
            redoPaths.removeAt(
                redoPaths.lastIndex
            )
        )

        invalidate()

        onDrawingChanged?.invoke()
    }

    fun clearDrawing() {

        paths.clear()
        redoPaths.clear()
        currentPath = null

        invalidate()

        onDrawingChanged?.invoke()
    }

    fun reset() {
        paths.clear()
        redoPaths.clear()
        currentPath = null
        currentColor = Color.WHITE
        currentSize = 14f
        eraserSize = 14f
        isEraser = false
        invalidate()
        onDrawingChanged?.invoke()
    }
}