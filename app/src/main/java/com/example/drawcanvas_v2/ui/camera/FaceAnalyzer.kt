package com.example.drawcanvas_v2.ui.camera

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer(
    private val overlayView: FaceOverlayView,
    private val isFrontCamera: () -> Boolean
) : ImageAnalysis.Analyzer {
    private val detector: FaceDetector =
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(
                    FaceDetectorOptions.PERFORMANCE_MODE_FAST
                )
                .setLandmarkMode(
                    FaceDetectorOptions.LANDMARK_MODE_ALL
                )
                .enableTracking()
                .build()
        )

    @OptIn(markerClass = [ExperimentalGetImage::class])
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage =
            imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotationDegrees =
            imageProxy.imageInfo.rotationDegrees
        val sourceWidth =
            if (rotationDegrees == 90 || rotationDegrees == 270) {
                imageProxy.height
            } else {
                imageProxy.width
            }
        val sourceHeight =
            if (rotationDegrees == 90 || rotationDegrees == 270) {
                imageProxy.width
            } else {
                imageProxy.height
            }
        val frontCamera = isFrontCamera()
        val inputImage =
            InputImage.fromMediaImage(
                mediaImage,
                rotationDegrees
            )
        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                overlayView.update(
                    faces,
                    sourceWidth,
                    sourceHeight,
                    frontCamera
                )
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Face detection failed", exception)
                overlayView.update(
                    emptyList(),
                    sourceWidth,
                    sourceHeight,
                    frontCamera
                )
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun close() {
        detector.close()
    }

    companion object {
        private const val TAG = "FaceAnalyzer"
    }
}
