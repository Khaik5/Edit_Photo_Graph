package com.example.drawcanvas_v2.ui.camera

sealed interface CameraEffect {
    data object RebindCamera : CameraEffect
    data object CapturePhoto : CameraEffect
    data object RequestAudioPermissionForVideo : CameraEffect
    data class StartVideoRecording(val withAudio: Boolean) : CameraEffect
    data object StopVideoRecording : CameraEffect
    data class SetZoomRatio(val ratio: Float) : CameraEffect
    data class SetExposure(val index: Int) : CameraEffect
    data class SetFlash(val state: FlashState, val captureMode: CaptureMode) : CameraEffect
    data class OpenPhotoPreview(val imageUri: String) : CameraEffect
    data class ShowMessage(val message: String) : CameraEffect
}
