package com.example.drawcanvas_v2.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face

class FaceOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var faces: List<Face> = emptyList()
    private var sourceWidth = 0
    private var sourceHeight = 0
    private var isFrontCamera = false
    private var filterMode = FilterMode.NONE
    private var filterBitmap: Bitmap? = null

    fun update(
        faces: List<Face>,
        sourceWidth: Int,
        sourceHeight: Int,
        isFrontCamera: Boolean
    ) {
        this.faces = faces
        this.sourceWidth = sourceWidth
        this.sourceHeight = sourceHeight
        this.isFrontCamera = isFrontCamera
        postInvalidate()
    }

    fun updateFilter(
        mode: FilterMode,
        bitmap: Bitmap?
    ) {
        filterMode = mode
        filterBitmap = bitmap
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap =
            filterBitmap ?: return
        if (
            filterMode == FilterMode.NONE ||
            faces.isEmpty() ||
            sourceWidth <= 0 ||
            sourceHeight <= 0
        ) {
            return
        }

        val face =
            faces.maxByOrNull {
                it.boundingBox.width() * it.boundingBox.height()
            } ?: return
        val matrix = DrawFilterHelper.getMatrix(
            sourceWidth = sourceWidth,
            sourceHeight = sourceHeight,
            viewWidth = width,
            viewHeight = height,
            isFrontCamera = isFrontCamera
        )

        when (filterMode) {
            FilterMode.HEAD -> {
                DrawFilterHelper.drawHeadFilter(
                    canvas,
                    face,
                    bitmap,
                    matrix
                )
            }
            FilterMode.CHEEK -> {
                DrawFilterHelper.drawCheekFilter(
                    canvas,
                    face,
                    bitmap,
                    matrix
                )
            }
            FilterMode.NONE -> Unit
        }
    }
}
