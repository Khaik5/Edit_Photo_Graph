package com.example.drawcanvas_v2.ui.camera

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector

data class CameraState(
    val flashState: FlashState = FlashState.OFF,
    val timerState: TimerState = TimerState.OFF,
    val cameraSize: CameraSize = CameraSize.S4_3,
    val gridState: Boolean = true,
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val filterMode: FilterMode = FilterMode.NONE,
    val filterBitmap: Bitmap? = null,
    val countdownValue: Int? = null,
    val isTakingPicture: Boolean = false,
    val brightness: Int = 0,
    val captureMode: CaptureMode = CaptureMode.PHOTO,
    val recordingState: RecordingState = RecordingState.IDLE,
    val recordingDurationMillis: Long = 0L,
    val zoomRatio: Float = 1f,
    val minZoomRatio: Float = 1f,
    val maxZoomRatio: Float = 1f
) {
    val isRecordingLocked: Boolean
        get() = recordingState != RecordingState.IDLE
}
