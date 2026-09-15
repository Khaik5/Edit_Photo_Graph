package com.example.drawcanvas_v2.ui.camera.photo

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import com.example.drawcanvas_v2.ui.camera.CameraImageProcessor
import com.example.drawcanvas_v2.ui.camera.CameraState
import com.example.drawcanvas_v2.ui.camera.FlashState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

class PhotoCaptureController {
    private var imageCapture: ImageCapture? = null

    fun create(rotation: Int, state: CameraState): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetRotation(rotation)
            .setFlashMode(state.flashState.toImageCaptureFlashMode())
            .build()
            .also { imageCapture = it }
    }

    fun capture(
        context: Context,
        state: CameraState,
        executor: Executor,
        scope: CoroutineScope,
        onPhotoReady: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val capture = imageCapture ?: run {
            onFailure("Camera photo capture is not ready")
            return
        }
        capture.flashMode = state.flashState.toImageCaptureFlashMode()
        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                processImage(context, image, state, scope, onPhotoReady, onFailure)
            }

            override fun onError(exception: ImageCaptureException) {
                scope.launch { onFailure("Cannot take photo") }
            }
        })
    }

    fun setFlash(flashState: FlashState) {
        imageCapture?.flashMode = flashState.toImageCaptureFlashMode()
    }

    private fun processImage(
        context: Context,
        image: ImageProxy,
        state: CameraState,
        scope: CoroutineScope,
        onPhotoReady: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        scope.launch {
            val cropped = try {
                withContext(Dispatchers.Default) {
                    var raw: android.graphics.Bitmap? = null
                    var oriented: android.graphics.Bitmap? = null
                    try {
                        raw = CameraImageProcessor.imageProxyToBitmap(image)
                        val rotation = image.imageInfo.rotationDegrees
                        oriented = CameraImageProcessor.rotateAndMirror(
                            raw,
                            rotation,
                            state.cameraSelector == androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
                        )
                        val result = CameraImageProcessor.cropToCameraSize(
                            oriented,
                            state.cameraSize
                        )
                        if (oriented !== result && !oriented!!.isRecycled) oriented!!.recycle()
                        if (raw !== result && raw !== oriented && !raw!!.isRecycled) raw!!.recycle()
                        result
                    } catch (error: Throwable) {
                        if (oriented != null && oriented !== raw && !oriented!!.isRecycled) oriented!!.recycle()
                        if (raw != null && !raw!!.isRecycled) raw!!.recycle()
                        throw error
                    } finally {
                        image.close()
                    }
                }
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                onFailure("Cannot process photo")
                return@launch
            }

            var filtered: android.graphics.Bitmap? = null
            try {
                filtered = CameraImageProcessor.applySelectedFilter(cropped, state)
                val uri = withContext(Dispatchers.IO) {
                    CameraImageProcessor.saveBitmapToCache(context, filtered!!)
                }
                onPhotoReady(uri.toString())
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                onFailure("Cannot process photo")
            } finally {
                if (filtered != null && filtered !== cropped && !filtered!!.isRecycled) filtered!!.recycle()
                if (!cropped.isRecycled) cropped.recycle()
            }
        }
    }

    private fun FlashState.toImageCaptureFlashMode(): Int = when (this) {
        FlashState.OFF -> ImageCapture.FLASH_MODE_OFF
        FlashState.AUTO -> ImageCapture.FLASH_MODE_AUTO
        FlashState.ON -> ImageCapture.FLASH_MODE_ON
    }
}
