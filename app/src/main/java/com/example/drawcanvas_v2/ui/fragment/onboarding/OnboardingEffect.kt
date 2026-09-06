package com.example.drawcanvas_v2.ui.fragment.onboarding
sealed interface OnboardingEffect {
    data object OpenGallery : OnboardingEffect
}