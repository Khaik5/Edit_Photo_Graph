package com.example.drawcanvas_v2.ui.preview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class PreviewViewModel : ViewModel() {

    private val _state =
        MutableStateFlow(
            PreviewState()
        )

    val state =
        _state.asStateFlow()

    private val _effect =
        Channel<PreviewEffect>(
            Channel.BUFFERED
        )

    val effect =
        _effect.receiveAsFlow()

    fun onAction(
        action: PreviewAction
    ) {
        when (action) {
            is PreviewAction.Load -> {
                _state.value =
                    PreviewState(
                         imageUri =
                            action.imageUri
                    )
            }
            PreviewAction.Back -> {
                _effect.trySend(
                    PreviewEffect.Finish
                )
            }
            PreviewAction.Edit -> {
                val uri = _state.value.imageUri ?: return
                _effect.trySend(PreviewEffect.OpenEditor(uri)
                )
            }
        }
    }
}