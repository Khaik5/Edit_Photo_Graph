package com.example.drawcanvas_v2.ui.camera

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private val _effect = Channel<CameraEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var countdownJob: Job? = null

    fun onAction(action: CameraAction) {
        when (action) {
            CameraAction.CaptureClicked -> onCaptureClicked()
            CameraAction.CaptureFinished -> finishPhotoCapture()
            is CameraAction.PhotoProcessed -> photoProcessed(action.imageUri)
            is CameraAction.PhotoProcessingFailed -> photoProcessingFailed(action.message)
            CameraAction.CameraSwapped -> swapCamera()
            CameraAction.FlashToggled -> toggleFlash()
            is CameraAction.CaptureModeSelected -> selectCaptureMode(action.mode)
            is CameraAction.TimerSelected -> selectTimer(action.timer)
            is CameraAction.SizeSelected -> selectSize(action.size)
            CameraAction.GridToggled -> toggleGrid()
            is CameraAction.FilterSelected -> selectFilter(action.mode, action.bitmap)
            is CameraAction.BrightnessSelected -> selectBrightness(action.value)
            is CameraAction.ZoomRequested -> requestZoom(action.ratio)
            is CameraAction.ZoomStateChanged -> updateZoomState(action)
            is CameraAction.AudioPermissionResult -> startVideoAfterAudioResult(action.granted)
            CameraAction.VideoRecordingStarted -> recordingStarted()
            is CameraAction.VideoDurationChanged -> updateRecordingDuration(action.durationMillis)
            is CameraAction.VideoRecordingFinalized -> recordingFinalized(action.outputUri)
            is CameraAction.VideoRecordingFailed -> recordingFailed(action.message)
        }
    }

    private fun onCaptureClicked() {
        val current = _state.value
        if (current.countdownValue != null) {
            cancelCountdown()
            return
        }
        if (current.isTakingPicture || current.recordingState == RecordingState.STARTING ||
            current.recordingState == RecordingState.STOPPING
        ) return

        if (current.captureMode == CaptureMode.VIDEO && current.recordingState == RecordingState.RECORDING) {
            _state.update { it.copy(recordingState = RecordingState.STOPPING) }
            _effect.trySend(CameraEffect.StopVideoRecording)
            return
        }
        startCountdownOrCapture(current.captureMode)
    }

    private fun startCountdownOrCapture(mode: CaptureMode) {
        val seconds = _state.value.timerState.seconds()
        if (seconds == 0) {
            beginCapture(mode)
            return
        }
        countdownJob = viewModelScope.launch {
            for (value in seconds downTo 1) {
                _state.update { it.copy(countdownValue = value) }
                delay(1_000)
            }
            _state.update { it.copy(countdownValue = null) }
            beginCapture(mode)
        }
    }

    private fun beginCapture(mode: CaptureMode) {
        when (mode) {
            CaptureMode.PHOTO -> {
                _state.update { it.copy(isTakingPicture = true) }
                _effect.trySend(CameraEffect.CapturePhoto)
            }
            CaptureMode.VIDEO -> {
                _state.update {
                    it.copy(recordingState = RecordingState.STARTING, recordingDurationMillis = 0L)
                }
                _effect.trySend(CameraEffect.RequestAudioPermissionForVideo)
            }
        }
    }

    private fun finishPhotoCapture() {
        _state.update { it.copy(countdownValue = null, isTakingPicture = false) }
    }

    private fun photoProcessed(imageUri: String) {
        finishPhotoCapture()
        _effect.trySend(CameraEffect.OpenPhotoPreview(imageUri))
    }

    private fun photoProcessingFailed(message: String) {
        finishPhotoCapture()
        _effect.trySend(CameraEffect.ShowMessage(message))
    }

    private fun selectCaptureMode(mode: CaptureMode) {
        if (_state.value.isRecordingLocked || _state.value.captureMode == mode) return
        cancelCountdown()
        _state.update {
            it.copy(
                captureMode = mode,
                flashState = FlashState.OFF,
                filterMode = if (mode == CaptureMode.PHOTO) it.filterMode else FilterMode.NONE,
                filterBitmap = if (mode == CaptureMode.PHOTO) it.filterBitmap else null,
                recordingDurationMillis = 0L
            )
        }
        _effect.trySend(CameraEffect.RebindCamera)
        _effect.trySend(CameraEffect.SetFlash(FlashState.OFF, mode))
    }

    private fun swapCamera() {
        if (_state.value.isRecordingLocked) return
        cancelCountdown()
        _state.update {
            it.copy(
                cameraSelector = if (it.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else CameraSelector.DEFAULT_BACK_CAMERA,
                flashState = FlashState.OFF,
                zoomRatio = 1f,
                minZoomRatio = 1f,
                maxZoomRatio = 1f
            )
        }
        _effect.trySend(CameraEffect.RebindCamera)
    }

    private fun toggleFlash() {
        val current = _state.value
        val next = if (current.captureMode == CaptureMode.VIDEO) {
            if (current.flashState == FlashState.ON) FlashState.OFF else FlashState.ON
        } else when (current.flashState) {
            FlashState.OFF -> FlashState.AUTO
            FlashState.AUTO -> FlashState.ON
            FlashState.ON -> FlashState.OFF
        }
        _state.update { it.copy(flashState = next) }
        _effect.trySend(CameraEffect.SetFlash(next, current.captureMode))
    }

    private fun selectTimer(timer: TimerState) {
        if (_state.value.isRecordingLocked) return
        cancelCountdown()
        _state.update { it.copy(timerState = timer) }
    }

    private fun selectSize(size: CameraSize) {
        if (_state.value.isRecordingLocked) return
        _state.update { it.copy(cameraSize = size) }
    }

    private fun toggleGrid() {
        _state.update { it.copy(gridState = !it.gridState) }
    }

    private fun selectFilter(mode: FilterMode, bitmap: Bitmap?) {
        if (_state.value.isRecordingLocked || _state.value.captureMode != CaptureMode.PHOTO) return
        _state.update { it.copy(filterMode = mode, filterBitmap = bitmap) }
    }

    private fun selectBrightness(value: Int) {
        _state.update { it.copy(brightness = value) }
        _effect.trySend(CameraEffect.SetExposure(value))
    }

    private fun requestZoom(ratio: Float) {
        val current = _state.value
        val clamped = ratio.coerceIn(current.minZoomRatio, current.maxZoomRatio)
        _state.update { it.copy(zoomRatio = clamped) }
        _effect.trySend(CameraEffect.SetZoomRatio(clamped))
    }

    private fun updateZoomState(action: CameraAction.ZoomStateChanged) {
        _state.update {
            it.copy(
                zoomRatio = action.zoomRatio,
                minZoomRatio = action.minZoomRatio,
                maxZoomRatio = action.maxZoomRatio
            )
        }
    }

    private fun startVideoAfterAudioResult(granted: Boolean) {
        if (_state.value.recordingState != RecordingState.STARTING) return
        if (!granted) _effect.trySend(CameraEffect.ShowMessage("Microphone unavailable: recording video without audio"))
        _effect.trySend(CameraEffect.StartVideoRecording(granted))
    }

    private fun recordingStarted() {
        if (_state.value.recordingState == RecordingState.STARTING) {
            _state.update { it.copy(recordingState = RecordingState.RECORDING) }
        }
    }

    private fun updateRecordingDuration(durationMillis: Long) {
        if (_state.value.recordingState == RecordingState.RECORDING) {
            _state.update { it.copy(recordingDurationMillis = durationMillis.coerceAtLeast(0L)) }
        }
    }

    private fun recordingFinalized(outputUri: String?) {
        _state.update {
            it.copy(recordingState = RecordingState.IDLE, recordingDurationMillis = 0L)
        }
        if (outputUri != null) _effect.trySend(CameraEffect.ShowMessage("Video saved"))
    }

    private fun recordingFailed(message: String) {
        _state.update {
            it.copy(recordingState = RecordingState.IDLE, recordingDurationMillis = 0L)
        }
        _effect.trySend(CameraEffect.ShowMessage(message))
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        _state.update { it.copy(countdownValue = null, isTakingPicture = false) }
    }

    override fun onCleared() {
        cancelCountdown()
        super.onCleared()
    }

    private fun TimerState.seconds(): Int = when (this) {
        TimerState.OFF -> 0
        TimerState.T_3 -> 3
        TimerState.T_5 -> 5
        TimerState.T_10 -> 10
    }
}
