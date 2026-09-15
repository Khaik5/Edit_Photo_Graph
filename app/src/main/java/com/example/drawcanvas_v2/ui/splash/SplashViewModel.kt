package com.example.drawcanvas_v2.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()
    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    private var started = false
    fun onAction(
        action: SplashAction
    ) {
        when (action) {
            SplashAction.Start -> {
                startSplash()
            }
        }
    }

    private fun startSplash() {
        if (started) {
            return
        }
        started = true
        viewModelScope.launch {
            _state.value = SplashState(isLoading = true)
            delay(2000)
            _state.value = SplashState(isLoading = false)
            _effect.send(SplashEffect.OpenHome)
        }
    }
}
