package com.example.drawcanvas_v2.ui.canvas_edit.crop

import android.graphics.Bitmap
import android.util.Log
import com.canhub.cropper.CropImageView
import com.example.drawcanvas_v2.databinding.ActivityCanvasEditBinding
import com.example.drawcanvas_v2.utils.CanvasResetUtils
class CropController(
    private val binding: ActivityCanvasEditBinding
) {
    private var sourceBitmap: Bitmap? = null
    private var committedBitmap: Bitmap? = null
    fun enter(): Boolean {
        binding.stickerView.clearSelection()
        binding.cropImageView.clearImage()
        releaseSourceBitmap()
        val bitmap = CropProcessor.renderEditor(binding) ?: return false
        sourceBitmap = bitmap
        binding.cropImageView.setImageBitmap(bitmap)
        binding.cropImageView.scaleType = CropImageView.ScaleType.FIT_CENTER
        binding.cropImageView.guidelines = CropImageView.Guidelines.ON
        return true
    }

    fun crop(): Bitmap? {
        val source = sourceBitmap ?: return null
        val croppedBitmap =
            try {
                binding.cropImageView.getCroppedImage(
                        0,
                        0,
                        CropImageView
                            .RequestSizeOptions
                            .NONE
                    )

            } catch (
                error: OutOfMemoryError
            ) {
                Log.e(TAG, "Not enough memory to crop image", error)
                null
            } catch (
                exception: Exception
            ) {
                Log.e(TAG, "Cannot crop image", exception)
                null
            }

        if (
            croppedBitmap == null
        ) {
            return null
        }
        binding.cropImageView.clearImage()
        sourceBitmap = null
        if (
            croppedBitmap !== source &&
            !source.isRecycled
        ) {
            source.recycle()
        }

        return croppedBitmap
    }
    fun commit(
        bitmap: Bitmap
    ) {
        CanvasResetUtils.reset(
            binding
        )

        val oldBitmap =
            committedBitmap
        binding.ivImage.setImageBitmap(bitmap)
        committedBitmap = bitmap
        if (
            oldBitmap != null &&
            oldBitmap !== bitmap &&
            !oldBitmap.isRecycled
        ) {
            oldBitmap.recycle()
        }
    }
    fun cancel() {
        binding.cropImageView.clearImage()
        releaseSourceBitmap()
    }
    fun clearCommittedBitmap() {

        val bitmap = committedBitmap
        committedBitmap = null
        if (
            bitmap != null && !bitmap.isRecycled
        ) {
            binding.ivImage.setImageDrawable(null)
            bitmap.recycle()
        }
    }
    fun release() {
        cancel()
        clearCommittedBitmap()
    }

    private fun releaseSourceBitmap() {

        val bitmap =
            sourceBitmap

        sourceBitmap =
            null

        if (
            bitmap != null &&
            !bitmap.isRecycled
        ) {

            bitmap.recycle()
        }
    }

    companion object {
        private const val TAG = "CropController"
    }
}
