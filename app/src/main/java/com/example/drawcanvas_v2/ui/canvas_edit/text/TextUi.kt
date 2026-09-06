package com.example.drawcanvas_v2.ui.canvas_edit.text

import android.graphics.Color
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawcanvas_v2.databinding.PanelTextBinding
import com.example.drawcanvas_v2.data.model.TextColorItem
import com.example.drawcanvas_v2.data.model.FontItem
import com.example.drawcanvas_v2.data.repository.FontRepository
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditAction
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditState
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditViewModel
import com.example.drawcanvas_v2.ui.canvas_edit.sticker.StickerView

class TextUi(
    private val binding: PanelTextBinding,
    private val stickerView: StickerView,
    private val viewModel: CanvasEditViewModel
) {

    private val fontRepository =
        FontRepository()

    fun setup() {
        binding.rvTextTools.layoutManager = horizontalManager()
        binding.rvTextTools.adapter = TextToolAdapter(TextTool.entries) { tool ->
                viewModel.onAction(
                    CanvasEditAction.TextToolChanged(tool)
                )
            }

        binding.rvFonts.layoutManager =
            horizontalManager()

        binding.rvFonts.adapter =
            FontAdapter(
                fontRepository.getFonts()
            ) { font ->

                viewModel.onAction(
                    CanvasEditAction.FontChanged(
                        font
                    )
                )

                selectedText()
                    ?.setFont(
                        font.fontRes
                    )

                stickerView.invalidate()

                changed()
            }

        binding.rvColors.layoutManager =
            horizontalManager()

        binding.rvColors.adapter =
            TextColorAdapter(
                colors()
            ) { item ->

                viewModel.onAction(
                    CanvasEditAction.TextColorChanged(
                        item.color
                    )
                )

                selectedText()
                    ?.setTextColor(
                        item.color
                    )

                stickerView.invalidate()
                changed()
            }

        binding.seekTextSize
            .setOnSeekBarChangeListener(
                listener { progress ->

                    val size =
                        progress.coerceIn(
                            10,
                            100
                        )

                    viewModel.onAction(
                        CanvasEditAction.TextSizeChanged(
                            size
                        )
                    )

                    selectedText()
                        ?.setTextSize(
                            size.toFloat()
                        )

                    stickerView.invalidate()

                    changed()
                }
            )

        binding.seekStroke
            .setOnSeekBarChangeListener(
                listener { progress ->

                    viewModel.onAction(
                        CanvasEditAction.StrokeChanged(
                            progress.coerceIn(
                                0,
                                20
                            )
                        )
                    )

                    selectedText()
                        ?.setStrokeWidth(
                            progress.toFloat()
                        )

                    stickerView.invalidate()

                    changed()
                }
            )
    }

    fun render(
        state: CanvasEditState
    ) {
        binding.rvFonts.visibility = View.GONE
        binding.rvColors.visibility = View.GONE
        binding.layoutSize.visibility = View.GONE
        binding.layoutStroke.visibility = View.GONE
        when (state.textTool) {
            TextTool.FONT -> binding.rvFonts.visibility = View.VISIBLE
            TextTool.COLOR -> binding.rvColors.visibility = View.VISIBLE
            TextTool.SIZE -> binding.layoutSize.visibility = View.VISIBLE
            TextTool.STROKE -> binding.layoutStroke.visibility = View.VISIBLE
        }

        binding.tvSizeValue.text =
            state.textSize.toString()

        binding.seekTextSize.progress =
            state.textSize

        binding.tvStrokeValue.text =
            state.stroke.toString()

        binding.seekStroke.progress =
            state.stroke
    }

    private fun selectedText():
            TextSticker? {

        return stickerView
            .getSelectedSticker()
                as? TextSticker
    }

    private fun changed() {

        viewModel.onAction(
            CanvasEditAction.Changed
        )
    }

    private fun horizontalManager() =
        LinearLayoutManager(
            binding.root.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

    private fun listener(
        callback: (Int) -> Unit
    ) =
        object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    callback(progress)
                }
            }

            override fun onStartTrackingTouch(
                seekBar: SeekBar?
            ) = Unit

            override fun onStopTrackingTouch(
                seekBar: SeekBar?
            ) = Unit
        }

    private fun colors():
            List<TextColorItem> {

        return listOf(
            TextColorItem(Color.WHITE),
            TextColorItem(Color.BLACK),
            TextColorItem(Color.RED),
            TextColorItem(Color.GREEN),
            TextColorItem(Color.BLUE),
            TextColorItem(Color.YELLOW),
            TextColorItem(Color.CYAN),
            TextColorItem(Color.MAGENTA)
        )
    }
}