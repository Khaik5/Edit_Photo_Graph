package com.example.drawcanvas_v2.ui.camera.ui

import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.example.drawcanvas_v2.R
import com.example.drawcanvas_v2.databinding.ActivityCameraBinding
import com.example.drawcanvas_v2.ui.camera.CameraAction
import com.example.drawcanvas_v2.ui.camera.CameraSize
import com.example.drawcanvas_v2.ui.camera.CameraState
import com.example.drawcanvas_v2.ui.camera.CaptureMode
import com.example.drawcanvas_v2.ui.camera.FilterMode
import com.example.drawcanvas_v2.ui.camera.FlashState
import com.example.drawcanvas_v2.ui.camera.RecordingState
import com.example.drawcanvas_v2.ui.camera.TimerState
import java.util.Locale

class CameraUiController(
    private val binding: ActivityCameraBinding
) {
    private var isUpdatingExposureUi = false
    private var isBrightnessPanelOpen = false
    private var isSizePanelOpen = false
    private var isTimerPanelOpen = false

    fun bindActions(
        onAction: (CameraAction) -> Unit,
        onClose: () -> Unit,
        stateProvider: () -> CameraState,
        exposureRangeProvider: () -> IntRange?
    ) {
        setupZoomGesture(onAction, stateProvider)
        binding.btnClose.setOnClickListener { onClose() }
        binding.btnCamera.setOnClickListener { onAction(CameraAction.CaptureClicked) }
        binding.btnSwap.setOnClickListener { onAction(CameraAction.CameraSwapped) }
        binding.btnFlash.setOnClickListener { onAction(CameraAction.FlashToggled) }
        binding.btnPhotoMode.setOnClickListener {
            onAction(CameraAction.CaptureModeSelected(CaptureMode.PHOTO))
        }
        binding.btnVideoMode.setOnClickListener {
            onAction(CameraAction.CaptureModeSelected(CaptureMode.VIDEO))
        }
        binding.btnZoom1x.setOnClickListener { onAction(CameraAction.ZoomRequested(1f)) }
        binding.btnZoom2x.setOnClickListener { onAction(CameraAction.ZoomRequested(2f)) }
        binding.btnZoom3x.setOnClickListener { onAction(CameraAction.ZoomRequested(3f)) }
        binding.btnArrow.setOnClickListener { toggleControls() }
        binding.btnLight.setOnClickListener { setBrightnessPanelOpen(!isBrightnessPanelOpen) }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isUpdatingExposureUi) {
                    val range = exposureRangeProvider() ?: return
                    onAction(
                        CameraAction.BrightnessSelected(
                            (range.first + progress).coerceIn(range.first, range.last)
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        binding.btnFilter.setOnClickListener {
            setBrightnessPanelOpen(false)
            setSizePanelOpen(false)
            setTimerPanelOpen(false)
            binding.scrollLayoutFilter.isVisible = !binding.scrollLayoutFilter.isVisible
        }
        binding.optionFilterNone.setOnClickListener { selectFilter(onAction, FilterMode.NONE, null) }
        binding.optionFilterHeadRabbit.setOnClickListener {
            selectFilter(onAction, FilterMode.HEAD, loadBitmap(R.drawable.filter_rabbit))
        }
        binding.optionFilterCheekRabbit.setOnClickListener {
            selectFilter(onAction, FilterMode.CHEEK, loadBitmap(R.drawable.filter_rabbit_cheek))
        }
        binding.optionFilterUnicorn.setOnClickListener {
            selectFilter(onAction, FilterMode.HEAD, loadBitmap(R.drawable.filter_unicorn))
        }
        binding.btnSize.setOnClickListener {
            setBrightnessPanelOpen(false)
            setTimerPanelOpen(false)
            setSizePanelOpen(!isSizePanelOpen)
        }
        binding.option11.setOnClickListener { selectSize(onAction, CameraSize.S1_1) }
        binding.option43.setOnClickListener { selectSize(onAction, CameraSize.S4_3) }
        binding.option169.setOnClickListener { selectSize(onAction, CameraSize.S16_9) }
        binding.optionFull.setOnClickListener { selectSize(onAction, CameraSize.SFull) }
        binding.btnGrid.setOnClickListener {
            setBrightnessPanelOpen(false)
            setSizePanelOpen(false)
            setTimerPanelOpen(false)
            onAction(CameraAction.GridToggled)
        }
        binding.btnTimer.setOnClickListener {
            setBrightnessPanelOpen(false)
            setSizePanelOpen(false)
            val opening = !isTimerPanelOpen
            setTimerPanelOpen(opening)
            if (opening && stateProvider().timerState == TimerState.OFF) {
                onAction(CameraAction.TimerSelected(TimerState.T_3))
            }
        }
        binding.optionOff.setOnClickListener { selectTimer(onAction, TimerState.OFF) }
        binding.optionT3.setOnClickListener { selectTimer(onAction, TimerState.T_3) }
        binding.optionT5.setOnClickListener { selectTimer(onAction, TimerState.T_5) }
        binding.optionT10.setOnClickListener { selectTimer(onAction, TimerState.T_10) }
    }

    fun render(state: CameraState, hasFlash: Boolean, exposureRange: IntRange?) {
        binding.overlayView.setGrid(state.gridState)
        binding.overlayView.setCameraSize(state.cameraSize)
        binding.overlayView.setCountdown(state.countdownValue)
        binding.tvCountdown.isVisible = state.countdownValue != null
        binding.tvCountdown.text = state.countdownValue?.toString().orEmpty()
        binding.tvGrid.setImageResource(if (state.gridState) R.drawable.ic_grid_on else R.drawable.ic_grid_off)
        binding.icBtnTimer.setImageResource(
            if (state.timerState == TimerState.OFF) R.drawable.ic_clock_off else R.drawable.ic_clock_on
        )
        val timerSeconds = state.timerState.seconds()
        binding.tvTimerValue.isVisible = timerSeconds > 0
        binding.tvTimerValue.text = if (timerSeconds > 0) timerSeconds.toString() else ""
        binding.textCurrentSize.text = state.cameraSize.label()
        binding.faceOverlayView.updateFilter(
            if (state.captureMode == CaptureMode.VIDEO && state.isRecordingLocked) FilterMode.NONE else state.filterMode,
            state.filterBitmap
        )
        binding.tvLight.text = state.brightness.toString()
        renderSelectionColors(state)
        renderCaptureMode(state)
        renderRecording(state)
        renderZoom(state)
        renderRecordingLock(state)
        renderFlash(state, hasFlash)
        renderExposure(state, exposureRange)
    }

    private fun setupZoomGesture(onAction: (CameraAction) -> Unit, stateProvider: () -> CameraState) {
        val detector = ScaleGestureDetector(
            binding.root.context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    onAction(CameraAction.ZoomRequested(stateProvider().zoomRatio * detector.scaleFactor))
                    return true
                }
            }
        )
        val listener = View.OnTouchListener { _, event ->
            detector.onTouchEvent(event)
            event.actionMasked != MotionEvent.ACTION_UP || detector.isInProgress
        }
        binding.previewView.setOnTouchListener(listener)
        binding.faceOverlayView.setOnTouchListener(listener)
        binding.overlayView.setOnTouchListener(listener)
    }

    private fun renderCaptureMode(state: CameraState) {
        val photo = state.captureMode == CaptureMode.PHOTO
        setModeSelected(binding.btnPhotoMode, binding.indicatorPhotoMode, photo)
        setModeSelected(binding.btnVideoMode, binding.indicatorVideoMode, !photo)
        binding.btnCamera.setImageResource(
            when {
                state.countdownValue != null -> R.drawable.ic_pause
                photo -> R.drawable.ic_rounded_white
                state.recordingState == RecordingState.RECORDING || state.recordingState == RecordingState.STOPPING -> R.drawable.ic_video_stop
                else -> R.drawable.ic_video_record
            }
        )
    }

    private fun renderRecording(state: CameraState) {
        val visible = state.recordingState == RecordingState.RECORDING || state.recordingState == RecordingState.STOPPING
        binding.tvRecordingDuration.isVisible = visible
        binding.tvRecordingDuration.text = "🔴 ${state.recordingDurationMillis.toDurationText()}"
    }

    private fun renderZoom(state: CameraState) {
        binding.tvZoomRatio.text = String.format(Locale.US, "%.1fx", state.zoomRatio)
        binding.btnZoom1x.isVisible = state.supportsZoomPreset(1f)
        binding.btnZoom2x.isVisible = state.supportsZoomPreset(2f)
        binding.btnZoom3x.isVisible = state.supportsZoomPreset(3f)
        setZoomSelected(binding.btnZoom1x, state.zoomRatio, 1f)
        setZoomSelected(binding.btnZoom2x, state.zoomRatio, 2f)
        setZoomSelected(binding.btnZoom3x, state.zoomRatio, 3f)
    }

    private fun renderRecordingLock(state: CameraState) {
        val enabled = !state.isRecordingLocked
        setEnabled(binding.btnPhotoMode, enabled)
        setEnabled(binding.btnVideoMode, enabled)
        setEnabled(binding.btnSwap, enabled)
        setEnabled(binding.btnSize, enabled)
        setEnabled(binding.btnTimer, enabled)
        setEnabled(binding.btnFilter, enabled)
        if (!enabled) {
            binding.scrollLayoutFilter.isVisible = false
            setSizePanelOpen(false)
            setTimerPanelOpen(false)
        }
    }

    private fun renderFlash(state: CameraState, hasFlash: Boolean) {
        binding.btnFlash.isEnabled = hasFlash
        binding.btnFlash.alpha = if (hasFlash) 1f else 0.35f
        binding.btnFlash.setImageResource(
            when (state.flashState) {
                FlashState.OFF -> R.drawable.ic_flash_off
                FlashState.AUTO -> R.drawable.ic_flash_auto
                FlashState.ON -> R.drawable.ic_flash_on
            }
        )
    }

    private fun renderExposure(state: CameraState, range: IntRange?) {
        val supported = range != null && (range.first != 0 || range.last != 0)
        binding.btnLight.isEnabled = supported
        binding.btnLight.alpha = if (supported) 1f else 0.35f
        binding.layoutSeekBar.isVisible = supported && isBrightnessPanelOpen
        if (!supported) return
        val restored = state.brightness.coerceIn(range.first, range.last)
        isUpdatingExposureUi = true
        binding.seekBar.max = range.last - range.first
        binding.seekBar.progress = restored - range.first
        isUpdatingExposureUi = false
    }

    private fun renderSelectionColors(state: CameraState) {
        setSelected(binding.optionOff, state.timerState == TimerState.OFF)
        setSelected(binding.optionT3, state.timerState == TimerState.T_3)
        setSelected(binding.optionT5, state.timerState == TimerState.T_5)
        setSelected(binding.optionT10, state.timerState == TimerState.T_10)
        setSelected(binding.option11, state.cameraSize == CameraSize.S1_1)
        setSelected(binding.option43, state.cameraSize == CameraSize.S4_3)
        setSelected(binding.option169, state.cameraSize == CameraSize.S16_9)
        setSelected(binding.optionFull, state.cameraSize == CameraSize.SFull)
    }

    private fun selectFilter(onAction: (CameraAction) -> Unit, mode: FilterMode, bitmap: Bitmap?) {
        binding.scrollLayoutFilter.isVisible = false
        onAction(CameraAction.FilterSelected(mode, bitmap))
    }

    private fun selectSize(onAction: (CameraAction) -> Unit, size: CameraSize) {
        setSizePanelOpen(false)
        onAction(CameraAction.SizeSelected(size))
    }

    private fun selectTimer(onAction: (CameraAction) -> Unit, timer: TimerState) {
        setTimerPanelOpen(false)
        onAction(CameraAction.TimerSelected(timer))
    }

    private fun loadBitmap(resourceId: Int): Bitmap? {
        val drawable = AppCompatResources.getDrawable(binding.root.context, resourceId) ?: return null
        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 512
        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 512
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
            drawable.setBounds(0, 0, bitmap.width, bitmap.height)
            drawable.draw(android.graphics.Canvas(bitmap))
        }
    }

    private fun toggleControls() {
        binding.layoutBtn.isVisible = !binding.layoutBtn.isVisible
        binding.layoutZoom.isVisible = binding.layoutBtn.isVisible
        binding.icBtnArrow.setImageResource(
            if (binding.layoutBtn.isVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
        )
    }

    private fun setBrightnessPanelOpen(open: Boolean) {
        if (open) {
            setSizePanelOpen(false)
            setTimerPanelOpen(false)
        }
        isBrightnessPanelOpen = open && binding.btnLight.isEnabled
        binding.layoutSeekBar.isVisible = isBrightnessPanelOpen
        binding.layoutBtnFilter.isVisible = !isBrightnessPanelOpen
        binding.layoutBtnSize.isVisible = !isBrightnessPanelOpen
        binding.btnGrid.isVisible = !isBrightnessPanelOpen
        binding.layoutBtnTimer.isVisible = !isBrightnessPanelOpen
        if (isBrightnessPanelOpen) {
            binding.scrollLayoutFilter.isVisible = false
            binding.layoutSize.isVisible = false
            binding.layoutTimer.isVisible = false
            isSizePanelOpen = false
            isTimerPanelOpen = false
        }
    }

    private fun setSizePanelOpen(open: Boolean) {
        if (open) setTimerPanelOpen(false)
        isSizePanelOpen = open
        binding.layoutSize.isVisible = open
        binding.layoutSize.post { binding.layoutSize.smoothScrollTo(0, 0) }
        binding.btnLightLayout.isVisible = !open
        binding.layoutBtnFilter.isVisible = !open
        binding.btnGrid.isVisible = !open
        binding.layoutBtnTimer.isVisible = !open
        binding.layoutBtnSize.layoutParams = binding.layoutBtnSize.layoutParams.apply {
            width = if (open) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        }
        if (open) {
            binding.layoutSeekBar.isVisible = false
            binding.scrollLayoutFilter.isVisible = false
            binding.layoutTimer.isVisible = false
            isBrightnessPanelOpen = false
            isTimerPanelOpen = false
        }
    }

    private fun setTimerPanelOpen(open: Boolean) {
        isTimerPanelOpen = open
        binding.layoutTimer.isVisible = open
        binding.btnLightLayout.isVisible = !open
        binding.layoutBtnFilter.isVisible = !open
        binding.layoutBtnSize.isVisible = !open
        binding.btnGrid.isVisible = !open
        binding.layoutBtnTimer.layoutParams = binding.layoutBtnTimer.layoutParams.apply {
            width = if (open) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        }
        if (open) {
            binding.layoutSeekBar.isVisible = false
            binding.scrollLayoutFilter.isVisible = false
            binding.layoutSize.isVisible = false
            isBrightnessPanelOpen = false
            isSizePanelOpen = false
        }
    }

    private fun setModeSelected(button: TextView, indicator: View, selected: Boolean) {
        button.alpha = if (selected) 1f else 0.55f
        button.setTextColor(if (selected) 0xFFFFFFFF.toInt() else 0xFFB0B0B0.toInt())
        indicator.isVisible = selected
    }

    private fun setZoomSelected(view: TextView, current: Float, preset: Float) {
        view.alpha = if (kotlin.math.abs(current - preset) < 0.06f) 1f else 0.6f
    }

    private fun setEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        view.alpha = if (enabled) 1f else 0.4f
    }

    private fun setSelected(view: TextView, selected: Boolean) {
        view.alpha = if (selected) 1f else 0.55f
        view.setTextColor(if (selected) 0xFFFFEB3BL.toInt() else 0xFFFFFFFF.toInt())
    }

    private fun CameraSize.label(): String = when (this) {
        CameraSize.S1_1 -> "1:1"
        CameraSize.S4_3 -> "4:3"
        CameraSize.S16_9 -> "16:9"
        CameraSize.SFull -> "Full"
    }

    private fun TimerState.seconds(): Int = when (this) {
        TimerState.OFF -> 0
        TimerState.T_3 -> 3
        TimerState.T_5 -> 5
        TimerState.T_10 -> 10
    }

    private fun CameraState.supportsZoomPreset(preset: Float): Boolean = preset in minZoomRatio..maxZoomRatio

    private fun Long.toDurationText(): String {
        val seconds = this / 1_000
        return String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60)
    }
}
