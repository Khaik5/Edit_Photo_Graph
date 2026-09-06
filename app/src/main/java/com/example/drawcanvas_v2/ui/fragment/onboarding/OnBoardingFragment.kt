package com.example.drawcanvas_v2.ui.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.drawcanvas_v2.databinding.FragmentOnBoardingBinding
import com.example.drawcanvas_v2.utils.AnimationUtils

class OnBoardingFragment : Fragment() {

    private val binding by lazy { FragmentOnBoardingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        showImage()
    }

    private fun showImage() {
        val image = requireArguments().getInt(IMAGE)
        binding.ivOnboarding.setImageResource(image)
        AnimationUtils.fadeIn(binding.ivOnboarding)
    }

    companion object {
        private const val IMAGE = "IMAGE"
        fun newInstance(
            @DrawableRes
            image: Int
        ): OnBoardingFragment {
            return OnBoardingFragment().apply {
                    arguments = bundleOf().apply {
                        putInt(IMAGE, image)
                    }
                }
        }
    }
}