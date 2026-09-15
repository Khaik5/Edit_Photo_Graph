package com.example.drawcanvas_v2.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomePromotionItem(
    val id: Int,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    @StringRes val buttonTextRes: Int,
    @DrawableRes val backgroundRes: Int,
    @DrawableRes val imageRes: Int,
    @DrawableRes val actionIconRes: Int,
    val ratios: List<String> = emptyList()
)
