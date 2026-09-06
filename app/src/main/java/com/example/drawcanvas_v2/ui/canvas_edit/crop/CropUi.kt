package com.example.drawcanvas_v2.ui.canvas_edit.crop
import android.view.View
import com.canhub.cropper.CropImageView
import com.example.drawcanvas_v2.R
import com.example.drawcanvas_v2.databinding.PanelCropBinding
import kotlin.math.max

class CropUi(
    private val binding: PanelCropBinding,
    private val cropImageView: CropImageView,
    private val viewModel: CropViewModel
) {
    private var lastResetVersion = -1
    private val buttons: List<View>
        get() =
            listOf(
                binding.btnCropFree,
                binding.btnCropLock,
                binding.btnCrop11,
                binding.btnCrop34,
                binding.btnCrop916
            )
    fun setup() {
        setupCropView()
        binding.btnCropFree
            .setOnClickListener {
                selectRatio(CropRatio.FREE)
            }
        binding.btnCropLock.setOnClickListener {
                selectRatio(CropRatio.LOCK)
            }
        binding.btnCrop11.setOnClickListener {
                selectRatio(CropRatio.RATIO_1_1)
            }
        binding.btnCrop34.setOnClickListener {
                selectRatio(CropRatio.RATIO_3_4)
            }
        binding.btnCrop916.setOnClickListener {
                selectRatio(CropRatio.RATIO_9_16)
            }
    }
    private fun setupCropView() {
        cropImageView.apply {
            scaleType = CropImageView.ScaleType.FIT_CENTER
            cropShape = CropImageView.CropShape.RECTANGLE
            guidelines = CropImageView.Guidelines.ON
            setMultiTouchEnabled(true)
            setCenterMoveEnabled(true)
            isAutoZoomEnabled = true
            maxZoom = 4
        }
    }
    private fun selectRatio(
        ratio: CropRatio
    ) {
        viewModel.onAction(
            CropAction.SelectRatio(
                ratio
            )
        )
    }
    fun render(
        state: CropState
    ) {
        if (
            state.resetVersion != lastResetVersion
        ) {
            lastResetVersion = state.resetVersion
            resetCropView()
        }
        when (
            state.selectedRatio
        ) {
            CropRatio.FREE -> {
                cropImageView.clearAspectRatio()
            }
            CropRatio.LOCK -> {
                val rect = cropImageView.cropRect
                if (rect != null) {
                    cropImageView.setAspectRatio(
                            max(
                                1,
                                rect.width()
                            ),
                            max(
                                1,
                                rect.height()
                            )
                        )
                } else {
                    cropImageView
                        .setFixedAspectRatio(
                            true
                        )
                }
            }
            CropRatio.RATIO_1_1 -> {
                cropImageView.setAspectRatio(
                        1,
                        1
                    )
            }
            CropRatio.RATIO_3_4 -> {

                cropImageView.setAspectRatio(
                        3,
                        4
                    )
            }
            CropRatio.RATIO_9_16 -> {
                cropImageView
                    .setAspectRatio(
                        9,
                        16
                    )
            }
        }
        selectButton(
            state.selectedRatio
        )
    }
    private fun resetCropView() {
        cropImageView.clearAspectRatio()
        cropImageView.resetCropRect()
        cropImageView.scaleType = CropImageView.ScaleType.FIT_CENTER
    }
    private fun selectButton(
        ratio: CropRatio
    ) {
        val selectedView =
            when (ratio) {
                CropRatio.FREE -> binding.btnCropFree
                CropRatio.LOCK -> binding.btnCropLock
                CropRatio.RATIO_1_1 -> binding.btnCrop11
                CropRatio.RATIO_3_4 -> binding.btnCrop34
                CropRatio.RATIO_9_16 -> binding.btnCrop916
            }
        buttons.forEach { view ->
            if (
                view === selectedView
            ) {
                view.setBackgroundResource(
                    R.drawable.bg_selected_card
                )
            } else {
                view.setBackgroundResource(
                    android.R.color.transparent
                )
            }
        }
    }
}