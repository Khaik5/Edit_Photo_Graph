package com.example.drawcanvas_v2.data.repository

import com.example.drawcanvas_v2.R
import com.example.drawcanvas_v2.data.model.OnBoardingItem

class OnBoardingRepository {

    fun getItems(): List<OnBoardingItem> {

        return listOf(
            OnBoardingItem(
                R.drawable.img_fragment_1
            ),
            OnBoardingItem(
                R.drawable.img_fragment_2
            ),
            OnBoardingItem(
                R.drawable.img_fragment_3
            )
        )
    }
}