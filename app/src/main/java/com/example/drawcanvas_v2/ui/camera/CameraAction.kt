package com.example.drawcanvas_v2.ui.camera

import android.graphics.Bitmap

sealed interface CameraAction {
    data object CaptureClicked : CameraAction
    data object CaptureFinished : CameraAction
    data class PhotoProcessed(val imageUri: String) : CameraAction
    data class PhotoProcessingFailed(val message: String) : CameraAction
    data object CameraSwapped : CameraAction
    data object FlashToggled : CameraAction
    data class CaptureModeSelected(val mode: CaptureMode) : CameraAction
    data class TimerSelected(val timer: TimerState) : CameraAction
    data class SizeSelected(val size: CameraSize) : CameraAction
    data object GridToggled : CameraAction
    data class FilterSelected(val mode: FilterMode, val bitmap: Bitmap?) : CameraAction
    data class BrightnessSelected(val value: Int) : CameraAction
    data class ZoomRequested(val ratio: Float) : CameraAction
    data class ZoomStateChanged(
        val zoomRatio: Float,
        val minZoomRatio: Float,
        val maxZoomRatio: Float
    ) : CameraAction
    data class AudioPermissionResult(val granted: Boolean) : CameraAction
    data object VideoRecordingStarted : CameraAction
    data class VideoDurationChanged(val durationMillis: Long) : CameraAction
    data class VideoRecordingFinalized(val outputUri: String?) : CameraAction
    data class VideoRecordingFailed(val message: String) : CameraAction
}
