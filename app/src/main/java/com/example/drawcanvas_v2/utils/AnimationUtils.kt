package com.example.drawcanvas_v2.utils

import android.view.View

object AnimationUtils {

    fun fadeIn(
        view: View,
        duration: Long = 220L
    ) {
        view.animate().cancel()
        view.alpha = 0f

        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    fun scaleIn(
        view: View,
        duration: Long = 280L
    ) {
        view.animate().cancel()

        view.alpha = 0f
        view.scaleX = 0.92f
        view.scaleY = 0.92f

        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .start()
    }

    fun panelIn(
        view: View
    ) {
        view.animate().cancel()

        view.alpha = 0f
        view.translationY = 8f

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(180L)
            .start()
    }

    fun press(
        view: View
    ) {
        view.animate().cancel()

        view.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .setDuration(70L)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100L)
                    .start()
            }
            .start()
    }

    fun selected(
        view: View,
        selected: Boolean
    ) {
        view.animate().cancel()

        view.animate()
            .scaleX(
                if (selected) 1.03f else 1f
            )
            .scaleY(
                if (selected) 1.03f else 1f
            )
            .alpha(
                if (selected) 1f else 0.86f
            )
            .setDuration(120L)
            .start()
    }

    fun animateItems(
        parent: android.view.ViewGroup
    ) {
        for (
        index in 0 until parent.childCount
        ) {
            val child =
                parent.getChildAt(index)

            child.animate().cancel()

            child.alpha = 0f
            child.translationY = 6f

            child.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(
                    index * 12L
                )
                .setDuration(150L)
                .start()
        }
    }
    fun panelContentIn(
        view: View
    ) {

        view.animate().cancel()

        view.alpha = 0f
        view.translationY = 6f

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(160L)
            .start()
    }
}