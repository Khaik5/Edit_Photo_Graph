package com.example.drawcanvas_v2.ui.canvas_edit.adjustment

import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawcanvas_v2.databinding.PanelAdjustmentBinding
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditAction
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditState
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditViewModel
import com.example.drawcanvas_v2.utils.AnimationUtils

class AdjustmentUi(
    private val binding: PanelAdjustmentBinding,
    private val viewModel: CanvasEditViewModel
) {
    private var hasUserSelected = false
    private val adapter = AdjustmentAdapter { type -> hasUserSelected = true
            viewModel.onAction(
                CanvasEditAction.AdjustmentSelected(type)
            )
        }

    fun setup() {

        binding.rvAdjustment.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvAdjustment.adapter = adapter
        binding.seekAdjustment.max = 200
        binding.seekAdjustment.progress = 100
        binding.seekAdjustment
            .setOnSeekBarChangeListener(
                object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int, // 0 -- 200
                        fromUser: Boolean
                    ) {

                        if (!fromUser) return
                        viewModel.onAction(
                            CanvasEditAction.AdjustmentValueChanged(progress - 100)
                        )
                        viewModel.onAction(
                            CanvasEditAction.Changed
                        )
                    }

                    override fun onStartTrackingTouch(
                        seekBar: SeekBar?
                    ) = Unit

                    override fun onStopTrackingTouch(
                        seekBar: SeekBar?
                    ) = Unit
                }
            )
    }

    fun render(
        state: CanvasEditState
    ) {
        adapter.setSelected(
            if (hasUserSelected) {
                state.selectedAdjustment
            } else {
                null
            }
        )
        val value =
            when (
                state.selectedAdjustment
            ) {
                AdjustmentType.BRIGHTNESS -> state.brightness
                AdjustmentType.CONTRAST -> state.contrast
                AdjustmentType.SATURATION -> state.saturation
                AdjustmentType.HUE -> state.hue
            }

        binding.tvValue.text = value.toString()
        binding.seekAdjustment.progress = value + 100
//        binding.rvAdjustment.post {
//            AnimationUtils.animateItems(binding.rvAdjustment)
//        }
    }
}