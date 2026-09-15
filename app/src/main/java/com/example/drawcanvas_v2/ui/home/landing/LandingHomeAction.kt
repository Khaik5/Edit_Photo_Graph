package com.example.drawcanvas_v2.ui.home.landing

sealed interface LandingHomeAction {
    data object StartClicked : LandingHomeAction
    data class BannerChanged(val position: Int) : LandingHomeAction
}
