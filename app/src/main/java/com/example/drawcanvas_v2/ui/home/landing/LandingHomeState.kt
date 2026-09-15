package com.example.drawcanvas_v2.ui.home.landing

import com.example.drawcanvas_v2.data.model.HomeBannerItem
import com.example.drawcanvas_v2.data.model.HomePromotionItem

data class LandingHomeState(
    val banners: List<HomeBannerItem> = emptyList(),
    val promotions: List<HomePromotionItem> = emptyList(),
    val currentBanner: Int = 0
)
