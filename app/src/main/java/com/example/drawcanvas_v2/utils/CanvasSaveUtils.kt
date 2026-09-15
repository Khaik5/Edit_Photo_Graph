package com.example.drawcanvas_v2.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import com.example.drawcanvas_v2.databinding.ActivityCanvasEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CanvasSaveUtils {
    fun save(
        activity: Activity,
        binding: ActivityCanvasEditBinding,
        onResult: (Boolean) -> Unit
    ) {
        // xóa selection trên sticker không bị khung chọn
        binding.stickerView.clearSelection()
        // sau khi xong hết layout thì post vào Ui
        binding.editorContainer.post {
            val imageRect = ImageGeometryUtils.getDisplayRect(binding.ivImage)
            if (imageRect == null || imageRect.width() <= 0f || imageRect.height() <= 0f) {
                onResult(false)
                return@post
            }
            val drawable = binding.ivImage.drawable
            val sourceBitmap = (drawable as? BitmapDrawable)?.bitmap
            val outputWidth = (sourceBitmap?.width ?: drawable.intrinsicWidth).coerceAtLeast(1)
            val outputHeight = (sourceBitmap?.height ?: drawable.intrinsicHeight).coerceAtLeast(1)

            try {
                // Keep the original image resolution. The editor coordinates are in
                // display pixels, so the complete composition is scaled together.
                val bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val scaleX = outputWidth / imageRect.width()
                val scaleY = outputHeight / imageRect.height()
                canvas.scale(scaleX, scaleY)
                canvas.clipRect(RectF(0f, 0f, imageRect.width(), imageRect.height()))
                canvas.translate(-imageRect.left, -imageRect.top)

                // Stable editor stacking order: image, drawing, stickers/text, frame.
                binding.ivImage.draw(canvas)
                binding.drawView.draw(canvas)
                binding.stickerView.draw(canvas)
                binding.frameOverlay.draw(canvas)

                CoroutineScope(Dispatchers.IO).launch {
                    val result = ImageSaveUtils.save(activity, bitmap)
                    if (!bitmap.isRecycled) bitmap.recycle()
                    activity.runOnUiThread { onResult(result) }
                }
            } catch (_: OutOfMemoryError) {
                onResult(false)
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }
}
