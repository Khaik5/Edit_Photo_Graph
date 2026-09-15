package com.example.drawcanvas_v2.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import androidx.camera.core.ImageProxy
import androidx.core.content.FileProvider
import com.example.drawcanvas_v2.BuildConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object CameraImageProcessor {
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        if (imageProxy.format == ImageFormat.JPEG) {
            return jpegImageProxyToBitmap(imageProxy)
        }

        val nv21 =
            imageProxyToNv21(imageProxy)
        val yuvImage =
            YuvImage(
                nv21,
                ImageFormat.NV21,
                imageProxy.width,
                imageProxy.height,
                null
            )
        val outputStream =
            ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(
                0,
                0,
                imageProxy.width,
                imageProxy.height
            ),
            96,
            outputStream
        )
        val bytes =
            outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        ) ?: throw IllegalStateException("Cannot decode YUV camera frame")
    }

    fun rotateAndMirror(
        bitmap: Bitmap,
        rotationDegrees: Int,
        mirror: Boolean
    ): Bitmap {
        if (rotationDegrees == 0 && !mirror) {
            return bitmap
        }

        val matrix =
            Matrix().apply {
                postRotate(rotationDegrees.toFloat())
                if (mirror) {
                    postScale(-1f, 1f)
                }
            }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun cropToCameraSize(
        bitmap: Bitmap,
        cameraSize: CameraSize
    ): Bitmap {
        val landscapeRatio =
            when (cameraSize) {
                CameraSize.S1_1 -> 1f
                CameraSize.S4_3 -> 4f / 3f
                CameraSize.S16_9 -> 16f / 9f
                CameraSize.SFull -> return bitmap
            }

        // CameraSize labels describe the camera sensor in landscape. The bitmap has
        // already been rotated for the current device orientation, so a portrait
        // capture must use the reciprocal ratio (3:4 / 9:16). Without this, the
        // live guide is portrait while the saved image is cropped landscape.
        val targetRatio =
            if (bitmap.height > bitmap.width) {
                1f / landscapeRatio
            } else {
                landscapeRatio
            }

        val sourceRatio =
            bitmap.width / bitmap.height.toFloat()
        val cropWidth: Int
        val cropHeight: Int

        if (sourceRatio > targetRatio) {
            cropHeight = bitmap.height
            cropWidth = (cropHeight * targetRatio).toInt().coerceAtLeast(1)
        } else {
            cropWidth = bitmap.width
            cropHeight = (cropWidth / targetRatio).toInt().coerceAtLeast(1)
        }

        val left =
            ((bitmap.width - cropWidth) / 2).coerceAtLeast(0)
        val top =
            ((bitmap.height - cropHeight) / 2).coerceAtLeast(0)

        return Bitmap.createBitmap(
            bitmap,
            left,
            top,
            cropWidth.coerceAtMost(bitmap.width),
            cropHeight.coerceAtMost(bitmap.height)
        )
    }

    suspend fun applySelectedFilter(
        bitmap: Bitmap,
        state: CameraState
    ): Bitmap {
        val filterBitmap =
            state.filterBitmap

        if (
            state.filterMode == FilterMode.NONE ||
            filterBitmap == null
        ) {
            return bitmap
        }

        return withContext(Dispatchers.Default) {
            val detector =
                FaceDetection.getClient(
                    FaceDetectorOptions.Builder()
                        .setPerformanceMode(
                            FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
                        )
                        .setLandmarkMode(
                            FaceDetectorOptions.LANDMARK_MODE_ALL
                        )
                        .build()
                )

            try {
                val resultBitmap =
                    bitmap.copy(
                        Bitmap.Config.ARGB_8888,
                        true
                    )
                val faces =
                    detector.process(
                        InputImage.fromBitmap(
                            resultBitmap,
                            0
                        )
                    ).await()
                val face =
                    faces.maxByOrNull {
                        it.boundingBox.width() * it.boundingBox.height()
                    }

                if (face != null) {
                    val canvas =
                        Canvas(resultBitmap)
                    when (state.filterMode) {
                        FilterMode.HEAD -> {
                            DrawFilterHelper.drawHeadFilter(
                                canvas,
                                face,
                                filterBitmap
                            )
                        }
                        FilterMode.CHEEK -> {
                            DrawFilterHelper.drawCheekFilterResult(
                                canvas,
                                face,
                                filterBitmap
                            )
                        }
                        FilterMode.NONE -> Unit
                    }
                }

                resultBitmap
            } finally {
                detector.close()
            }
        }
    }

    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap
    ): Uri {
        val directory =
            File(
                context.cacheDir,
                "camera"
            ).apply {
                mkdirs()
            }
        val file =
            File(
                directory,
                "camera_${System.currentTimeMillis()}.jpg"
            )

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                95,
                outputStream
            )
        }

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )
    }
    // xữ lý khi mà chụp ảnh vào
    private fun imageProxyToNv21(imageProxy: ImageProxy): ByteArray {
        require(imageProxy.planes.size >= 3) {
            "Expected at least 3 planes for YUV image, got ${imageProxy.planes.size}"
        }

        val width =
            imageProxy.width
        val height =
            imageProxy.height
        val ySize =
            width * height
        val uvSize =
            width * height / 2
        val output =
            ByteArray(ySize + uvSize)

        val yPlane =
            imageProxy.planes[0]
        val uPlane =
            imageProxy.planes[1]
        val vPlane =
            imageProxy.planes[2]

        var outputOffset = 0
        for (row in 0 until height) {
            val rowOffset =
                row * yPlane.rowStride
            for (col in 0 until width) {
                output[outputOffset++] =
                    yPlane.buffer.get(
                        rowOffset + col * yPlane.pixelStride
                    )
            }
        }
        // đếm row size khi chụp ảnh
        for (row in 0 until height / 2) {
            val uRowOffset =
                row * uPlane.rowStride
            val vRowOffset =
                row * vPlane.rowStride
            for (col in 0 until width / 2) {
                output[outputOffset++] =
                    vPlane.buffer.get(
                        vRowOffset + col * vPlane.pixelStride
                    )
                output[outputOffset++] =
                    uPlane.buffer.get(
                        uRowOffset + col * uPlane.pixelStride
                    )
            }
        }

        return output
    }

    private fun jpegImageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer =
            imageProxy.planes.firstOrNull()?.buffer
                ?: throw IllegalStateException("JPEG camera image has no plane")
        val bytes =
            ByteArray(buffer.remaining())
        buffer.get(bytes)

        return BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        ) ?: throw IllegalStateException("Cannot decode JPEG camera image")
    }
}
