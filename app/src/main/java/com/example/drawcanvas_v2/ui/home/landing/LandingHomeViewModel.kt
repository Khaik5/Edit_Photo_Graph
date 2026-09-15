package com.example.drawcanvas_v2.ui.home.landing

import androidx.lifecycle.ViewModel
import com.example.drawcanvas_v2.data.repository.LandingHomeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class LandingHomeViewModel : ViewModel() {
    private val repository = LandingHomeRepository()
    private val _state = MutableStateFlow(
        LandingHomeState(
            banners = repository.getBanners(),
            promotions = repository.getPromotions()
        )
    )
    val state = _state.asStateFlow()
    private val _effect = Channel<LandingHomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onAction(action: LandingHomeAction) {
        when (action) {
            LandingHomeAction.StartClicked -> openOnboarding()
            is LandingHomeAction.BannerChanged -> changeBanner(action.position)
        }
    }

    private fun changeBanner(position: Int) {
        if (position in _state.value.banners.indices) {
            _state.value = _state.value.copy(currentBanner = position)
        }
    }

    private fun openOnboarding() {
        _effect.trySend(LandingHomeEffect.OpenOnboarding)
    }
}
