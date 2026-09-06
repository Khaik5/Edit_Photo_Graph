package com.example.drawcanvas_v2.ui.camera.camera

import android.content.Context
import android.util.Log
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.example.drawcanvas_v2.ui.camera.CameraState
import com.example.drawcanvas_v2.ui.camera.FaceAnalyzer
import com.example.drawcanvas_v2.ui.camera.FaceOverlayView
import java.util.concurrent.Executor

class CameraSessionController(
    private val context: Context
) {
    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var faceAnalyzer: FaceAnalyzer? = null

    fun start(onReady: () -> Unit) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(
            {
                provider = future.get()
                onReady()
            },
            context.mainExecutor
        )
    }

    fun bind(
        owner: LifecycleOwner,
        state: CameraState,
        previewView: PreviewView,
        faceOverlayView: FaceOverlayView,
        analyzerExecutor: Executor,
        captureUseCase: UseCase,
        onZoomStateChanged: (Float, Float, Float) -> Unit,
        onBound: () -> Unit
    ) {
        val cameraProvider = provider ?: return
        val viewPort = previewView.viewPort ?: run {
            previewView.post {
                bind(
                    owner,
                    state,
                    previewView,
                    faceOverlayView,
                    analyzerExecutor,
                    captureUseCase,
                    onZoomStateChanged,
                    onBound
                )
            }
            return
        }
        cameraProvider.unbindAll()
        faceAnalyzer?.close()

        val rotation = previewView.display?.rotation ?: Surface.ROTATION_0
        val preview = Preview.Builder()
            .setTargetRotation(rotation)
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val analyzer = FaceAnalyzer(
            overlayView = faceOverlayView,
            isFrontCamera = { state.cameraSelector == androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA }
        )
        faceAnalyzer = analyzer
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(rotation)
            .build()
            .also { it.setAnalyzer(analyzerExecutor, analyzer) }

        try {
            val group = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .addUseCase(captureUseCase)
                .setViewPort(viewPort)
                .build()
            camera = cameraProvider.bindToLifecycle(owner, state.cameraSelector, group)
            camera?.cameraInfo?.zoomState?.observe(owner) { zoom ->
                onZoomStateChanged(zoom.zoomRatio, zoom.minZoomRatio, zoom.maxZoomRatio)
            }
            onBound()
        } catch (exception: Exception) {
            Log.e(TAG, "Cannot bind camera use cases", exception)
        }
    }

    fun setZoomRatio(ratio: Float) {
        camera?.cameraControl?.setZoomRatio(ratio)
    }

    fun setTorch(enabled: Boolean) {
        camera?.cameraControl?.enableTorch(hasFlashUnit() && enabled)
    }

    fun setExposure(index: Int) {
        val range = exposureRange() ?: return
        if (range.first == 0 && range.last == 0) return
        camera?.cameraControl?.setExposureCompensationIndex(index.coerceIn(range.first, range.last))
    }

    fun hasFlashUnit(): Boolean = camera?.cameraInfo?.hasFlashUnit() == true

    fun exposureRange(): IntRange? {
        val range = camera?.cameraInfo?.exposureState?.exposureCompensationRange ?: return null
        return range.lower..range.upper
    }

    fun release() {
        faceAnalyzer?.close()
        faceAnalyzer = null
        provider?.unbindAll()
        camera = null
    }

    private companion object {
        const val TAG = "CameraSessionController"
    }
}
