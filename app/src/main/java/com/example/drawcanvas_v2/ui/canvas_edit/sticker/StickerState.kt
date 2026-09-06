package com.example.drawcanvas_v2.ui.canvas_edit.sticker

import com.example.drawcanvas_v2.data.model.StickerItem

data class StickerState(
    val loading: Boolean = false,
    val stickers: List<StickerItem> = emptyList()
)