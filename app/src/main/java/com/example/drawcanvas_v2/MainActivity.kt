package com.example.drawcanvas_v2

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.drawcanvas_v2.databinding.ActivityMainBinding
import com.example.drawcanvas_v2.ui.fragment.onboarding.AdapterFragment
import com.example.drawcanvas_v2.ui.fragment.onboarding.OnboardingAction
import com.example.drawcanvas_v2.ui.fragment.onboarding.OnboardingEffect
import com.example.drawcanvas_v2.ui.fragment.onboarding.OnboardingState
import com.example.drawcanvas_v2.ui.fragment.onboarding.OnboardingViewModel
import com.example.drawcanvas_v2.ui.home.GalleryActivity
import com.example.drawcanvas_v2.utils.AnimationUtils
import com.example.drawcanvas_v2.utils.InsetsUtils
import com.example.drawcanvas_v2.utils.collectFlow
import kotlin.getValue
import com.example.drawcanvas_v2.R
class MainActivity :
    AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: OnboardingViewModel by viewModels()
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )
        setContentView(binding.root)
        setupView()
        setupListener()
        observeViewModel()
    }

    private fun setupView() {

        InsetsUtils.apply(
            this,
            binding.root
        )

        setupViewPager()

        AnimationUtils.scaleIn(
            binding.btnGetStarted
        )
    }
    private fun setupViewPager() {
        val items = viewModel.state.value.items
        binding.viewPager.adapter =
            AdapterFragment(
                this,
                items
            )
    }
    private fun setupListener() {

        binding.viewPager
            .registerOnPageChangeCallback(
                object :
                    ViewPager2
                    .OnPageChangeCallback() {
                    override fun onPageSelected(
                        position: Int
                    ) {
                        viewModel.onAction(
                            OnboardingAction
                                .PageChanged(
                                    position
                                )
                        )
                    }
                }
            )

        binding.btnGetStarted
            .setOnClickListener {
                viewModel.onAction(
                    OnboardingAction
                        .GetStarted
                )
            }
    }

    private fun observeViewModel() {
        collectFlow(
            viewModel.state
        ) { state ->
            showState(state)
        }
        collectFlow(
            viewModel.effect
        ) { effect ->
            handleEffect(effect)
        }
    }
    private fun showState(
        state: OnboardingState
    ) {

        showIndicator(state.currentPage)
    }
    private fun showIndicator(
        position: Int
    ) {
        binding.indicator1.setBackgroundResource(getIndicator(position == 0))
        binding.indicator2.setBackgroundResource(getIndicator(position == 1))
        binding.indicator3.setBackgroundResource(getIndicator(position == 2))
    }

    private fun getIndicator(
        selected: Boolean
    ): Int {
        return if (selected) {
            R.drawable.bg_indicator_active
        } else {
            R.drawable.bg_indicator_inactive
        }
    }
    private fun handleEffect(
        effect: OnboardingEffect
    ) {
        when (effect) {
            OnboardingEffect.OpenGallery -> {
                openGallery()
            }
        }
    }
    private fun openGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
        finish()
    }
}