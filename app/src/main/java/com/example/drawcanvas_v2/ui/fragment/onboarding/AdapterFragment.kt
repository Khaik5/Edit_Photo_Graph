package com.example.drawcanvas_v2.ui.fragment.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.drawcanvas_v2.data.model.OnBoardingItem

class AdapterFragment(
    activity: FragmentActivity,
    private val items: List<OnBoardingItem>
) : FragmentStateAdapter(
    activity
) {
    override fun getItemCount(): Int {
        return items.size
    }
    override fun createFragment(
        position: Int
    ): Fragment {
        val item = items[position]
        return OnBoardingFragment.newInstance(item.image)
    }
}