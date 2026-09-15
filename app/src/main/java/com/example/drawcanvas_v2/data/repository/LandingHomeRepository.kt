package com.example.drawcanvas_v2.data.repository

import com.example.drawcanvas_v2.R
import com.example.drawcanvas_v2.data.model.HomeBannerItem
import com.example.drawcanvas_v2.data.model.HomePromotionItem

class LandingHomeRepository {
    fun getBanners(): List<HomeBannerItem> {
        return listOf(
            HomeBannerItem(
                id = 1,
                titleRes = R.string.landing_banner_drawing_title,
                descriptionRes = R.string.landing_banner_drawing_description,
                buttonTextRes = R.string.landing_banner_drawing_cta,
                backgroundRes = R.drawable.bg_landing_banner_purple,
                imageRes = R.drawable.ic_draw
            ),
            HomeBannerItem(
                id = 2,
                titleRes = R.string.landing_banner_text_title,
                descriptionRes = R.string.landing_banner_text_description,
                buttonTextRes = R.string.landing_banner_text_cta,
                backgroundRes = R.drawable.bg_landing_banner_pink,
                imageRes = R.drawable.ic_text
            ),
            HomeBannerItem(
                id = 3,
                titleRes = R.string.landing_banner_sticker_title,
                descriptionRes = R.string.landing_banner_sticker_description,
                buttonTextRes = R.string.landing_banner_sticker_cta,
                backgroundRes = R.drawable.bg_landing_banner_peach,
                imageRes = R.drawable.ic_sticker
            ),
            HomeBannerItem(
                id = 4,
                titleRes = R.string.landing_banner_filter_title,
                descriptionRes = R.string.landing_banner_filter_description,
                buttonTextRes = R.string.landing_banner_filter_cta,
                backgroundRes = R.drawable.bg_landing_banner_blue,
                imageRes = R.drawable.ic_filter
            )
        )
    }

    fun getPromotions(): List<HomePromotionItem> {
        return listOf(
            HomePromotionItem(
                id = 1,
                titleRes = R.string.landing_promotion_edit_title,
                subtitleRes = R.string.landing_promotion_edit_subtitle,
                buttonTextRes = R.string.landing_promotion_edit_cta,
                backgroundRes = R.drawable.bg_landing_promotion_blue,
                imageRes = R.drawable.home_promo_edit,
                actionIconRes = R.drawable.ic_edit
            ),
            HomePromotionItem(
                id = 2,
                titleRes = R.string.landing_promotion_sticker_title,
                subtitleRes = R.string.landing_promotion_sticker_subtitle,
                buttonTextRes = R.string.landing_promotion_sticker_cta,
                backgroundRes = R.drawable.bg_landing_promotion_peach,
                imageRes = R.drawable.home_promo_sticker,
                actionIconRes = R.drawable.ic_sticker
            ),
            HomePromotionItem(
                id = 3,
                titleRes = R.string.landing_promotion_canvas_title,
                subtitleRes = R.string.landing_promotion_canvas_subtitle,
                buttonTextRes = R.string.landing_promotion_canvas_cta,
                backgroundRes = R.drawable.bg_landing_promotion_mint,
                imageRes = R.drawable.ic_draw,
                actionIconRes = R.drawable.ic_draw,
                ratios = listOf("1:1", "3:4", "9:16")
            ),
            HomePromotionItem(
                id = 4,
                titleRes = R.string.landing_promotion_tools_title,
                subtitleRes = R.string.landing_promotion_tools_subtitle,
                buttonTextRes = R.string.landing_promotion_tools_cta,
                backgroundRes = R.drawable.bg_landing_banner_purple,
                imageRes = R.drawable.ic_draw,
                actionIconRes = R.drawable.ic_draw
            ),
            HomePromotionItem(
                id = 5,
                titleRes = R.string.landing_promotion_text_title,
                subtitleRes = R.string.landing_promotion_text_subtitle,
                buttonTextRes = R.string.landing_promotion_text_cta,
                backgroundRes = R.drawable.bg_landing_banner_pink,
                imageRes = R.drawable.ic_text,
                actionIconRes = R.drawable.ic_text
            ),
            HomePromotionItem(
                id = 6,
                titleRes = R.string.landing_promotion_filter_title,
                subtitleRes = R.string.landing_promotion_filter_subtitle,
                buttonTextRes = R.string.landing_promotion_filter_cta,
                backgroundRes = R.drawable.bg_landing_banner_blue,
                imageRes = R.drawable.ic_filter,
                actionIconRes = R.drawable.ic_filter
            ),
            HomePromotionItem(
                id = 7,
                titleRes = R.string.landing_promotion_frame_title,
                subtitleRes = R.string.landing_promotion_frame_subtitle,
                buttonTextRes = R.string.landing_promotion_frame_cta,
                backgroundRes = R.drawable.bg_landing_promotion_peach,
                imageRes = R.drawable.ic_frame,
                actionIconRes = R.drawable.ic_frame
            ),
            HomePromotionItem(
                id = 8,
                titleRes = R.string.landing_promotion_art_title,
                subtitleRes = R.string.landing_promotion_art_subtitle,
                buttonTextRes = R.string.landing_promotion_art_cta,
                backgroundRes = R.drawable.bg_landing_promotion_mint,
                imageRes = R.drawable.home_promo_canvas,
                actionIconRes = R.drawable.ic_draw
            )
        )
    }
}
