package com.example.drawcanvas_v2.ui.fragment.onboarding

sealed interface OnboardingAction {

    data class PageChanged(
        val position: Int
    ) : OnboardingAction
    data object GetStarted : OnboardingAction
}