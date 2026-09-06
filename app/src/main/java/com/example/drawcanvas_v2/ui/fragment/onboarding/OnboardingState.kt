package com.example.drawcanvas_v2.ui.fragment.onboarding

import com.example.drawcanvas_v2.data.model.OnBoardingItem

data class OnboardingState(
    val items: List<OnBoardingItem> = emptyList(),
    val currentPage: Int = 0
)