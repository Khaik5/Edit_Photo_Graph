package com.example.drawcanvas_v2.ui.splash

sealed interface SplashEffect {
    data object OpenOnboarding : SplashEffect
}