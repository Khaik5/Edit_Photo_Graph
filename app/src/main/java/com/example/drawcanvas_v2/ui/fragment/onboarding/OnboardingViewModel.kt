package com.example.drawcanvas_v2.ui.fragment.onboarding

import androidx.lifecycle.ViewModel
import com.example.drawcanvas_v2.data.repository.OnBoardingRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class OnboardingViewModel : ViewModel() {
    private val repository = OnBoardingRepository()
    private val _state = MutableStateFlow(OnboardingState(
            items = repository.getItems()
            ))
    val state = _state.asStateFlow()
    private val _effect = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    fun onAction(
        action:
        OnboardingAction
    ) {
        when (action) {
            is OnboardingAction.PageChanged -> {
                changePage(action.position)
            }
            OnboardingAction.GetStarted -> { openGallery() }
        }
    }

    private fun changePage(
        position: Int
    ) {
        _state.value = _state.value.copy(currentPage = position)
    }
    private fun openGallery() {
        _effect.trySend(
            OnboardingEffect.OpenGallery
        )
    }
}