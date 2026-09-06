package com.example.drawcanvas_v2.ui.canvas_edit.draw

import android.graphics.Color
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawcanvas_v2.databinding.PanelDrawBinding
import com.example.drawcanvas_v2.data.repository.DrawColorRepository
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditAction
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditState
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditViewModel
import com.example.drawcanvas_v2.utils.AnimationUtils

class DrawUi(
    private val binding: PanelDrawBinding,
    private val drawView: DrawView,
    private val viewModel: CanvasEditViewModel
) {

    private val repository =
        DrawColorRepository()

    private val toolAdapter =
        DrawToolAdapter { tool ->

            viewModel.onAction(
                CanvasEditAction.DrawToolChanged(
                    tool
                )
            )

            viewModel.onAction(
                CanvasEditAction.Changed
            )
        }

    private val colorAdapter =
        DrawColorAdapter(
            repository.getColors()
        ) { item ->

            viewModel.onAction(
                CanvasEditAction.DrawColorChanged(
                    item.color
                )
            )

            viewModel.onAction(
                CanvasEditAction.Changed
            )
        }

    fun setup() {

        setupToolRecycler()

        setupColorRecycler()

        setupSizeSlider()

        drawView.onDrawingChanged = {

            viewModel.onAction(
                CanvasEditAction.Changed
            )
        }
    }

    private fun setupToolRecycler() {

        binding.rvDrawTools.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvDrawTools.adapter =
            toolAdapter
    }

    private fun setupColorRecycler() {

        binding.rvDrawColors.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvDrawColors.adapter =
            colorAdapter
    }

    private fun setupSizeSlider() {

        binding.seekPaintSize.max =
            80

        binding.seekPaintSize.progress =
            14

        binding.seekPaintSize.setOnSeekBarChangeListener(
            object :
                SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    if (!fromUser) {
                        return
                    }

                    val value =
                        progress.coerceIn(
                            2,
                            80
                        )

                    binding.tvPaintSize.text =
                        value.toString()

                    viewModel.onAction(
                        CanvasEditAction
                            .DrawSizeChanged(
                                value
                            )
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

        binding.rvDrawColors.visibility =
            android.view.View.GONE

        binding.layoutPaintSize.visibility =
            android.view.View.GONE

        when (state.drawTool) {

            DrawTool.COLOR -> {

                drawView.usePen()

                drawView.setPenColor(
                    state.drawColor
                )

                drawView.setPenSize(
                    state.drawSize.toFloat()
                )

                drawView.setEraserSize(
                    state.drawSize.toFloat()
                )

                binding.rvDrawColors.visibility =
                    android.view.View.VISIBLE

                toolAdapter.setSelected(
                    DrawTool.COLOR
                )

                AnimationUtils.panelContentIn(
                    binding.rvDrawColors
                )
            }

            DrawTool.ERASER -> {

                drawView.useEraser()

                drawView.setEraserSize(
                    state.drawSize.toFloat()
                )

                toolAdapter.setSelected(
                    DrawTool.ERASER
                )
            }

            DrawTool.PAINT_SIZE -> {

                drawView.usePen()

                drawView.setPenSize(
                    state.drawSize.toFloat()
                )

                drawView.setEraserSize(
                    state.drawSize.toFloat()
                )

                binding.layoutPaintSize.visibility =
                    android.view.View.VISIBLE

                binding.seekPaintSize.progress =
                    state.drawSize

                binding.tvPaintSize.text =
                    state.drawSize.toString()

                toolAdapter.setSelected(
                    DrawTool.PAINT_SIZE
                )

                AnimationUtils.panelContentIn(
                    binding.layoutPaintSize
                )
            }
        }
    }

    fun reset() {

        drawView.reset()

        binding.seekPaintSize.progress =
            14

        binding.tvPaintSize.text =
            "14"

        toolAdapter.setSelected(
            null
        )

        colorAdapterReset()

        viewModel.onAction(
            CanvasEditAction.DrawToolChanged(
                DrawTool.COLOR
            )
        )

        viewModel.onAction(
            CanvasEditAction.DrawColorChanged(
                Color.WHITE
            )
        )

        viewModel.onAction(
            CanvasEditAction.DrawSizeChanged(
                14
            )
        )
    }

    private fun colorAdapterReset() {

        binding.rvDrawColors.scrollToPosition(
            0
        )
    }
}