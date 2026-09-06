package com.example.drawcanvas_v2.ui.splash

sealed interface SplashAction {
    data object Start : SplashAction
}