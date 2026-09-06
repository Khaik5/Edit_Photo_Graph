package com.example.drawcanvas_v2.ui.canvas_edit.sticker

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawcanvas_v2.databinding.PanelStickerBinding
import com.example.drawcanvas_v2.data.model.StickerItem
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditState
import com.example.drawcanvas_v2.utils.AnimationUtils

class StickerUi(
    private val binding: PanelStickerBinding,
    private val onStickerClick:
        (StickerItem) -> Unit
) {

    private val adapter =
        StickerAdapter(
            onStickerClick
        )

    fun setup() {

        binding.rvStickers.layoutManager =
            LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvStickers.adapter =
            adapter
    }

    fun render(
        state: CanvasEditState
    ) {

        adapter.submitList(
            state.stickers
        )

        if (
            state.stickers.isNotEmpty()
        ) {
            binding.rvStickers.post {
                AnimationUtils.animateItems(
                    binding.rvStickers
                )
            }
        }
    }
}