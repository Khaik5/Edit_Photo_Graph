package com.example.drawcanvas_v2.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

object ImageSaveUtils {

    fun save(
        context: Context, // truy cập vào bộ nhớ của đt
        bitmap: Bitmap
    ): Boolean {
        val resolver = context.contentResolver // cb hs cho ảnh, contentResolver giups cho tạo sửa xóa trong thư viện
        val values = ContentValues().apply { // như một tờ khai để đăng ký một bức ảnh mới với hệ thống
                put(
                    MediaStore.Images.Media.DISPLAY_NAME, // thời gian để không trùng tên
                    "DrawCanvas_${System.currentTimeMillis()}.jpg"
                )
                put(
                    MediaStore.Images.Media.MIME_TYPE,
                    "image/jpeg"
                )
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.Q
                ) {
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/DrawCanvas" // lưu vào thư mục ảnh của tôi
                    )
                    put(
                        MediaStore.Images.Media.IS_PENDING, 1 // đang ghi dở, file này chưa hoàn chỉnh
                    )
                }
            }
        // Hệ thống nhận được tờ khai tạo ra bản ghi mới và trả về một địa chỉ để ta ghi nội dung vào đó
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        return try {
            val success = resolver.openOutputStream(uri)?.use { // openOutputStream mở một ống dẫn để ghi dữ liệu tại địa chỉ Uri
                        bitmap.compress( // Nén bức ảnh thành định dạng JPEG với chất lượng 95% và ghi vào ống dẫn
                            Bitmap.CompressFormat.JPEG,
                            95,
                            it
                        )
                    } == true

            if (!success) {
                throw IllegalStateException()
            }
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q
            ) {
                resolver.update(uri, ContentValues().apply {
                        put(
                            MediaStore.Images.Media.IS_PENDING, 0
                        )
                    },
                    null,
                    null
                )
            }

            true

        } catch (
            exception: Exception
        ) {

            resolver.delete(
                uri,
                null,
                null
            )

            false
        }
    }
}