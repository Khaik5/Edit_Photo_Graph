package com.example.drawcanvas_v2.ui.canvas_edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawcanvas_v2.data.repository.FrameRepository
import com.example.drawcanvas_v2.data.repository.StickerRepository
import com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentType
import com.example.drawcanvas_v2.ui.canvas_edit.draw.DrawTool
import com.example.drawcanvas_v2.ui.canvas_edit.filter.FilterType
import com.example.drawcanvas_v2.ui.canvas_edit.text.TextSticker
import com.example.drawcanvas_v2.ui.canvas_edit.text.TextTool
import com.example.drawcanvas_v2.ui.canvas_edit.ui.CanvasTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CanvasEditViewModel : ViewModel() {

    private val stickerRepository = StickerRepository()

    private val frameRepository = FrameRepository()

    private val _state =
        MutableStateFlow(CanvasEditState())
    val state = _state.asStateFlow()
    private val _effect =
        Channel<CanvasEditEffect>(
            Channel.BUFFERED
        )

    val effect =
        _effect.receiveAsFlow()

    private var selectedText: TextSticker? = null
    fun onAction(
        action: CanvasEditAction
    ) {
        when (action) {
            is CanvasEditAction.ToolClick -> changeMode(action.tool)
            CanvasEditAction.Changed -> markChanged()
            CanvasEditAction.CropApplied -> cropApplied()
            CanvasEditAction.Back -> back()
            CanvasEditAction.ToolBack -> toolBack()
            CanvasEditAction.ToolDone -> toolDone()
            CanvasEditAction.Save -> save()
            CanvasEditAction.Restore -> restore()
            CanvasEditAction.RestoreConfirm -> restoreConfirm()
            CanvasEditAction.ResetCurrentTool -> resetCurrentTool()
            is CanvasEditAction.SaveResult -> saveResult(action.success)
            is CanvasEditAction.DrawToolChanged -> updateDrawTool(action.tool)
            is CanvasEditAction.DrawColorChanged -> updateDrawColor(action.color)
            is CanvasEditAction.DrawSizeChanged -> updateDrawSize(action.size)
            is CanvasEditAction.TextToolChanged -> _state.value =
                _state.value.copy(textTool = action.tool)

            is CanvasEditAction.FontChanged -> _state.value =
                _state.value.copy(selectedFont = action.font)

            is CanvasEditAction.TextColorChanged -> _state.value =
                _state.value.copy(textColor = action.color)

            is CanvasEditAction.TextSizeChanged ->
                _state.value = _state.value.copy(textSize = action.size.coerceIn(10, 100))

            is CanvasEditAction.StrokeChanged ->
                _state.value = _state.value.copy(stroke = action.stroke.coerceIn(0, 20))

            is CanvasEditAction.StickerLoaded ->
                _state.value = _state.value.copy(stickers = action.stickers)

            is CanvasEditAction.FrameLoaded ->
                _state.value = _state.value.copy(frames = action.frames)

            is CanvasEditAction.AdjustmentSelected ->
                _state.value = _state.value.copy(selectedAdjustment = action.type)

            is CanvasEditAction.AdjustmentValueChanged ->
                updateAdjustment(action.value)

            is CanvasEditAction.FilterSelected ->
                _state.value = _state.value.copy(selectedFilter = action.filter)
        }
    }

    fun setSelectedText(
        sticker: TextSticker?
    ) {
        selectedText = sticker
    }
    fun cropApplied(){
        _state.value = _state.value.copy(
            mode = EditorMode.MAIN, changed = true,
            brightness = 0,
            contrast = 0,
            saturation = 0,
            hue = 0,
            selectedFilter = FilterType.NONE
        )
    }
    fun getSelectedText(): TextSticker? = selectedText
    fun loadStickers(
        context: Context
    ) {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                stickerRepository.getStickers(context)
            }
            onAction(
                CanvasEditAction.StickerLoaded(items)
            )
        }
    }

    fun loadFrames(
        context: Context
    ) {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                frameRepository.getFrames(context)
            }
            onAction(CanvasEditAction.FrameLoaded(items))
        }
    }

    private fun updateDrawTool(tool: DrawTool) {
        _state.value = _state.value.copy(drawTool = tool)
    }

    private fun updateDrawColor(color: Int) {
        _state.value = _state.value.copy(drawColor = color)
    }

    private fun updateDrawSize(size: Int) {
        _state.value = _state.value.copy(drawSize = size.coerceIn(2, 80))
    }

    private fun resetCurrentTool() {
        when (state.value.mode) {
            EditorMode.DRAW -> {
                _state.value = _state.value.copy(
                    drawTool = DrawTool.COLOR,
                    drawColor = android.graphics.Color.WHITE,
                    drawSize = 14
                )
            }

            EditorMode.TEXT -> {
                _state.value = _state.value.copy(
                    textTool = TextTool.FONT,
                    selectedFont = null,
                    textColor = android.graphics.Color.WHITE,
                    textSize = 36,
                    stroke = 0
                )
            }

            EditorMode.ADJUSTMENT -> {
                _state.value = _state.value.copy(
                    brightness = 0,
                    contrast = 0,
                    saturation = 0,
                    hue = 0
                )
            }

            EditorMode.FILTER -> {
                _state.value = _state.value.copy(
                    selectedFilter = FilterType.NONE
                )
            }
            EditorMode.CROP,
            EditorMode.STICKER,
            EditorMode.FRAME,
            EditorMode.MAIN -> Unit
        }
    }

    private fun changeMode(
        tool: CanvasTool
    ) {
        val mode = when (tool) {
                CanvasTool.STICKER -> EditorMode.STICKER
                CanvasTool.DRAW -> EditorMode.DRAW
                CanvasTool.TEXT -> EditorMode.TEXT
                CanvasTool.FRAME -> EditorMode.FRAME
                CanvasTool.ADJUSTMENT -> EditorMode.ADJUSTMENT
                CanvasTool.FILTER -> EditorMode.FILTER
                CanvasTool.CROP -> EditorMode.CROP
            }
        _state.value = _state.value.copy(mode = mode)
    }

    private fun updateAdjustment(
        value: Int
    ) {
        val safeValue =
            value.coerceIn(-100, 100)
        _state.value = when (
                _state.value.selectedAdjustment
            ) {
                AdjustmentType.BRIGHTNESS -> _state.value.copy(brightness = safeValue)
                AdjustmentType.CONTRAST -> _state.value.copy(contrast = safeValue)
                AdjustmentType.SATURATION -> _state.value.copy(saturation = safeValue)
                AdjustmentType.HUE -> _state.value.copy(hue = safeValue)
            }
    }
    private fun markChanged() {
        _state.value = _state.value.copy(changed = true)
    }

    private fun toolBack() {
        _state.value = _state.value.copy(mode = EditorMode.MAIN)
    }

    private fun toolDone() {
        _state.value = _state.value.copy(mode = EditorMode.MAIN)
    }

    private fun save() {
        if (_state.value.saving) { // tránh lưu nhiều lần
            return
        }
        _state.value = _state.value.copy(saving = true) // cập nhật trạng thái mới nhất
        _effect.trySend(
            CanvasEditEffect.SaveImage
        )
    }

    private fun saveResult(
        success: Boolean
    ) {
        _state.value = _state.value.copy(
            saving = false,
            changed =
                if (success) {
                    false
                } else {
                    _state.value.changed
                }
        )
        _effect.trySend(
            CanvasEditEffect.ShowMessage(
                if (success) {
                    "Image saved"
                } else {
                    "Save failed"
                }
            )
        )
    }

    private fun restore() {
        // kiểm tra có thay đổi nào khng
        if (!_state.value.changed) {
            return
        }
        _effect.trySend(CanvasEditEffect.ConfirmRestore)
    }

    private fun restoreConfirm() {
        // đặt toàn state về mặc định
        _state.value = CanvasEditState()
            // xóa text sticker đang chọn
        selectedText = null
        _effect.trySend(
            CanvasEditEffect.RestoreImage
        )
    }

    private fun back() {
        if (
            _state.value.mode != EditorMode.MAIN
        ) {
            toolBack()
        } else if (_state.value.changed
        ) {
            _effect.trySend(
                CanvasEditEffect.ConfirmExit
            )
        } else {
            _effect.trySend(CanvasEditEffect.Finish)
        }
    }
}