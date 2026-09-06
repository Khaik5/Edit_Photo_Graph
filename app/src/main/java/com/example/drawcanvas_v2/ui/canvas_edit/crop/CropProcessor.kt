package com.example.drawcanvas_v2.ui.canvas_edit.crop

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.example.drawcanvas_v2.databinding.ActivityCanvasEditBinding
import com.example.drawcanvas_v2.utils.ImageGeometryUtils
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

object CropProcessor {
    private const val MAX_DIMENSION =
        4096

    private const val MAX_PIXELS =
        12_000_000L
    fun renderEditor(
        binding:
        ActivityCanvasEditBinding
    ): Bitmap? {
        val imageRect =
            ImageGeometryUtils
                .getDisplayRect(
                    binding.ivImage
                )
                ?: return null
        if (
            imageRect.width() <= 0f ||
            imageRect.height() <= 0f
        ) {
            return null
        }
        val drawable = binding.ivImage.drawable ?: return null
        var outputWidth =
            if (
                drawable.intrinsicWidth > 0
            ) {
                drawable.intrinsicWidth
            } else {
                imageRect.width().roundToInt()
            }
        var outputHeight =
            if (
                drawable.intrinsicHeight > 0
            ) {
                drawable.intrinsicHeight
            } else {
                imageRect.height().roundToInt()
            }
        outputWidth = outputWidth.coerceAtLeast(1)
        outputHeight = outputHeight.coerceAtLeast(1)
        val dimensionScale =
            min(
                1f,

                MAX_DIMENSION.toFloat() /
                        maxOf(
                            outputWidth,
                            outputHeight
                        )
            )
        val pixelCount =
            outputWidth.toLong() *
                    outputHeight.toLong()

        val pixelScale =
            if (
                pixelCount >
                MAX_PIXELS
            ) {
                sqrt(
                    MAX_PIXELS.toDouble() /
                            pixelCount.toDouble()
                ).toFloat()

            } else {

                1f
            }
        val finalScale =
            min(
                dimensionScale,
                pixelScale
            )
        outputWidth = (outputWidth * finalScale)
                .roundToInt()
                .coerceAtLeast(1)
        outputHeight = (outputHeight * finalScale)
                .roundToInt()
                .coerceAtLeast(1)

        return try {
            val bitmap =
                Bitmap.createBitmap(
                    outputWidth,
                    outputHeight,
                    Bitmap.Config.ARGB_8888
                )
            val canvas =
                Canvas(
                    bitmap
                )
            val scaleX =
                outputWidth / imageRect.width()
            val scaleY =
                outputHeight / imageRect.height()
            canvas.scale(
                scaleX,
                scaleY
            )
            canvas.translate(
                -imageRect.left,
                -imageRect.top
            )
            binding.ivImage.draw(
                canvas
            )
            binding.drawView.draw(
                canvas
            )
            binding.stickerView.draw(
                canvas
            )
            binding.frameOverlay.draw(
                canvas
            )
            bitmap

        } catch (
            error: OutOfMemoryError
        ) {
            Log.e(TAG, "Not enough memory to render editor for crop", error)
            null
        } catch (
            exception: Exception
        ) {
            Log.e(TAG, "Cannot render editor for crop", exception)
            null
        }
    }

    private const val TAG =
        "CropProcessor"
}
