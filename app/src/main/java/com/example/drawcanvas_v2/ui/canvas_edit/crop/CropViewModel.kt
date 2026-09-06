package com.example.drawcanvas_v2.ui.canvas_edit.crop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CropViewModel : ViewModel() {
    private val _state = MutableStateFlow(CropState())
    val state = _state.asStateFlow()
    fun onAction(
        action: CropAction
    ) {
        when (action) {
            is CropAction.SelectRatio -> {
                _state.value = _state.value.copy(
                    selectedRatio = action.ratio
                )
            }
            CropAction.Reset -> {
                _state.value = CropState(
                        selectedRatio = CropRatio.FREE,
                        resetVersion = _state.value.resetVersion + 1
                    )
            }
        }
    }
}