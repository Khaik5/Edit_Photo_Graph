package com.example.drawcanvas_v2.ui.home.landing

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.drawcanvas_v2.MainActivity
import com.example.drawcanvas_v2.R
import com.example.drawcanvas_v2.databinding.ActivityLandingHomeBinding
import com.example.drawcanvas_v2.ui.home.landing.banner.HomeBannerAdapter
import com.example.drawcanvas_v2.ui.home.landing.promotion.HomePromotionAdapter
import com.example.drawcanvas_v2.utils.InsetsUtils
import com.example.drawcanvas_v2.utils.collectFlow

class LandingHomeActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLandingHomeBinding.inflate(layoutInflater)
    }
    private val viewModel: LandingHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupRecyclerView()
        setupViewPager()
        setupListener()
        observeViewModel()
    }

    private fun setupView() {
        InsetsUtils.apply(this, binding.root)
    }

    private fun setupRecyclerView() {
        binding.rvPromotions.layoutManager = LinearLayoutManager(this)
        binding.rvPromotions.adapter = HomePromotionAdapter(viewModel.state.value.promotions)
    }

    private fun setupViewPager() {
        binding.vpPromotions.adapter = HomeBannerAdapter(viewModel.state.value.banners)
        binding.vpPromotions.setCurrentItem(viewModel.state.value.currentBanner, false)
    }

    private fun setupListener() {
        binding.btnStart.setOnClickListener {
            viewModel.onAction(LandingHomeAction.StartClicked)
        }
        binding.vpPromotions.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.onAction(LandingHomeAction.BannerChanged(position))
                }
            }
        )
    }

    private fun observeViewModel() {
        collectFlow(viewModel.state) { state ->
            showState(state)
        }
        collectFlow(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun showState(state: LandingHomeState) {
        showIndicator(state.currentBanner)
    }

    private fun showIndicator(position: Int) {
        binding.indicator1.setBackgroundResource(getIndicator(position == 0))
        binding.indicator2.setBackgroundResource(getIndicator(position == 1))
        binding.indicator3.setBackgroundResource(getIndicator(position == 2))
        binding.indicator4.setBackgroundResource(getIndicator(position == 3))
    }

    private fun getIndicator(selected: Boolean): Int {
        return if (selected) {
            R.drawable.bg_landing_indicator_selected
        } else {
            R.drawable.bg_landing_indicator_inactive
        }
    }

    private fun handleEffect(effect: LandingHomeEffect) {
        when (effect) {
            LandingHomeEffect.OpenOnboarding -> openOnboarding()
        }
    }

    private fun openOnboarding() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
