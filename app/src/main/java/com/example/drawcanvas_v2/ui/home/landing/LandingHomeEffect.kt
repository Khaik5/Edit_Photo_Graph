package com.example.drawcanvas_v2.ui.home.landing

sealed interface LandingHomeEffect {
    data object OpenOnboarding : LandingHomeEffect
}
