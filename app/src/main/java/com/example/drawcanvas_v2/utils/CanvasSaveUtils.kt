package com.example.drawcanvas_v2.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
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
            // lấy vùng hiển ảnh góc trên màn hình
            val imageRect = ImageGeometryUtils.getDisplayRect(binding.ivImage)
            if (imageRect == null || imageRect.width() <= 0f || imageRect.height() <= 0f) {
                onResult(false)
                return@post
            }
            // xác định bipmap đầu ra là gì
            val outputWidth = imageRect.width().toInt().coerceAtLeast(1)
            val outputHeight = imageRect.height().toInt().coerceAtLeast(1)
            // tạo bipmap với kích cỡ ảnh đó
            val bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            // tạo ma trận scale và translate để vẽ đúng vị trí đó
            val scaleX = outputWidth / imageRect.width()
            val scaleY = outputHeight / imageRect.height()
            canvas.scale(scaleX, scaleY)
            canvas.clipRect(RectF(0f, 0f, imageRect.width(), imageRect.height()))
            canvas.translate(-imageRect.left, -imageRect.top)
            // vẽ theo thứ tự lần lượt là ảnh góc, nét vẽ, Sticker/text, frame
            binding.ivImage.draw(canvas)
            binding.drawView.draw(canvas)
            binding.stickerView.draw(canvas)
            binding.frameOverlay.draw(canvas)
            // đây là hàm lưu bipmap về ổ cứng (IO thread)
            CoroutineScope(Dispatchers.IO).launch {
                val result = ImageSaveUtils.save(activity, bitmap)
                bitmap.recycle()
                activity.runOnUiThread { onResult(result) }
            }
        }
    }
}
