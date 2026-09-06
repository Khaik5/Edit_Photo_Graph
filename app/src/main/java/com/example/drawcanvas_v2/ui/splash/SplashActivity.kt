package com.example.drawcanvas_v2.ui.splash
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.drawcanvas_v2.databinding.ActivitySplashBinding
import com.example.drawcanvas_v2.MainActivity
import com.example.drawcanvas_v2.utils.AnimationUtils
import com.example.drawcanvas_v2.utils.InsetsUtils
import com.example.drawcanvas_v2.utils.collectFlow

class SplashActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater)}
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        observeViewModel()
        viewModel.onAction(SplashAction.Start)
    }
    private fun setupView() {
        InsetsUtils.apply(this, binding.root)
        AnimationUtils.fadeIn(binding.tvTitle)
        AnimationUtils.scaleIn(binding.ivLogo)
    }
    private fun observeViewModel() {
        collectFlow(viewModel.state) { state ->
            showState(state)
        }
        collectFlow(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }
    private fun showState(
        state: SplashState
    ) {

        binding.loadingView.isVisible = state.isLoading
    }
    private fun handleEffect(
        effect: SplashEffect
    ) {
        when (effect) {
            SplashEffect.OpenOnboarding -> { openOnboarding() }
        }
    }
    private fun openOnboarding() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}