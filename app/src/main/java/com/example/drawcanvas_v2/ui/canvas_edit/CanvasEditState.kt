package com.example.drawcanvas_v2.ui.canvas_edit

import android.graphics.Color
import com.example.drawcanvas_v2.data.model.FrameItem
import com.example.drawcanvas_v2.data.model.StickerItem
import com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentType
import com.example.drawcanvas_v2.ui.canvas_edit.draw.DrawTool
import com.example.drawcanvas_v2.ui.canvas_edit.filter.FilterType
import com.example.drawcanvas_v2.data.model.FontItem
import com.example.drawcanvas_v2.ui.canvas_edit.text.TextTool

data class CanvasEditState(
    val mode: EditorMode = EditorMode.MAIN,
    val changed: Boolean = false,
    val saving: Boolean = false,

    val drawTool: DrawTool = DrawTool.COLOR,
    val drawColor: Int = Color.WHITE,
    val drawSize: Int = 14,

    val textTool: TextTool = TextTool.FONT,
    val selectedFont: FontItem? = null,
    val textColor: Int = Color.WHITE,
    val textSize: Int = 36,
    val stroke: Int = 0,

    val stickers: List<StickerItem> = emptyList(),
    val frames: List<FrameItem> = emptyList(),

    val selectedAdjustment: AdjustmentType =
        AdjustmentType.BRIGHTNESS,

    val brightness: Int = 0,
    val contrast: Int = 0,
    val saturation: Int = 0,
    val hue: Int = 0,

    val selectedFilter: FilterType =
        FilterType.NONE
)