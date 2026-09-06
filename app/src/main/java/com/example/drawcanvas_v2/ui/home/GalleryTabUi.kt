package com.example.drawcanvas_v2.ui.home

import android.graphics.Color
import com.example.drawcanvas_v2.databinding.ActivityHomeBinding

class GalleryTabUi(
    private val binding:
    ActivityHomeBinding
) {
    private val selectedColor =
        Color.parseColor(
            "#18181B"
        )
    private val normalColor =
        Color.parseColor(
            "#8A8A92"
        )
    fun show(
        position: Int
    ) {

        binding.tabRecents
            .setTextColor(
                getColor(
                    position == 0
                )
            )
        binding.tabFavourites
            .setTextColor(
                getColor(
                    position == 1
                )
            )
        binding.tabSelfies
            .setTextColor(
                getColor(
                    position == 2
                )
            )
        binding.lineRecents.alpha =
            getAlpha(
                position == 0
            )

        binding.lineFavourites.alpha =
            getAlpha(
                position == 1
            )

        binding.lineSelfies.alpha =
            getAlpha(
                position == 2
            )
    }
    private fun getColor(
        selected: Boolean
    ): Int {
        return if (selected) {

            selectedColor
        } else {

            normalColor
        }
    }

    private fun getAlpha(
        selected: Boolean
    ): Float {
        return if (selected) {
            1f

        } else {
            0f
        }
    }
}