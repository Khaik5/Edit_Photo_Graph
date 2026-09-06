package com.example.drawcanvas_v2.ui.home
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.drawcanvas_v2.data.model.GalleryType
import com.example.drawcanvas_v2.ui.fragment.gallery.GalleryFragment

class GalleryViewPageAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(
    activity
) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(
        position: Int
    ): Fragment {
        val type =
            when (position) {
                1 -> {
                    GalleryType.FAVOURITES
                }
                2 -> {
                    GalleryType.SELFIES
                }
                else -> {
                    GalleryType.RECENTS
                }
            }
        return GalleryFragment.newInstance(type)
    }
}