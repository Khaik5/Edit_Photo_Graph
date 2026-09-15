package com.example.drawcanvas_v2.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomeBannerItem(
    val id: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val buttonTextRes: Int,
    @DrawableRes val backgroundRes: Int,
    @DrawableRes val imageRes: Int
)
