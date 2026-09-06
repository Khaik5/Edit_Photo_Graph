package com.example.drawcanvas_v2.ui.canvas_edit

import com.example.drawcanvas_v2.data.model.FrameItem
import com.example.drawcanvas_v2.data.model.StickerItem
import com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentType
import com.example.drawcanvas_v2.ui.canvas_edit.draw.DrawTool
import com.example.drawcanvas_v2.ui.canvas_edit.filter.FilterType
import com.example.drawcanvas_v2.data.model.FontItem
import com.example.drawcanvas_v2.ui.canvas_edit.text.TextTool
import com.example.drawcanvas_v2.ui.canvas_edit.ui.CanvasTool

sealed interface CanvasEditAction {

    data class ToolClick(
        val tool: CanvasTool
    ) : CanvasEditAction

    data object Changed : CanvasEditAction
    data object CropApplied : CanvasEditAction

    data object Back : CanvasEditAction

    data object ToolBack : CanvasEditAction

    data object ToolDone : CanvasEditAction

    data object Save : CanvasEditAction

    data object Restore : CanvasEditAction

    data object RestoreConfirm : CanvasEditAction
    data object ResetCurrentTool : CanvasEditAction

    data class SaveResult(
        val success: Boolean
    ) : CanvasEditAction

    data class DrawToolChanged(
        val tool: DrawTool
    ) : CanvasEditAction

    data class DrawColorChanged(
        val color: Int
    ) : CanvasEditAction

    data class DrawSizeChanged(
        val size: Int
    ) : CanvasEditAction

    data class TextToolChanged(
        val tool: TextTool
    ) : CanvasEditAction

    data class FontChanged(
        val font: FontItem
    ) : CanvasEditAction

    data class TextColorChanged(
        val color: Int
    ) : CanvasEditAction

    data class TextSizeChanged(
        val size: Int
    ) : CanvasEditAction

    data class StrokeChanged(
        val stroke: Int
    ) : CanvasEditAction

    data class StickerLoaded(
        val stickers: List<StickerItem>
    ) : CanvasEditAction

    data class FrameLoaded(
        val frames: List<FrameItem>
    ) : CanvasEditAction

    data class AdjustmentSelected(
        val type: AdjustmentType
    ) : CanvasEditAction

    data class AdjustmentValueChanged(
        val value: Int
    ) : CanvasEditAction

    data class FilterSelected(
        val filter: FilterType
    ) : CanvasEditAction
}