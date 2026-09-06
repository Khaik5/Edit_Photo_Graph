package com.example.drawcanvas_v2.data.repository

import android.graphics.Color
import com.example.drawcanvas_v2.data.model.DrawColorItem

class DrawColorRepository {
    fun getColors(): List<DrawColorItem> {
        return listOf(
            DrawColorItem(Color.WHITE),
            DrawColorItem(Color.BLACK),
            DrawColorItem(Color.RED),
            DrawColorItem(Color.GREEN),
            DrawColorItem(Color.BLUE),
            DrawColorItem(Color.YELLOW),
            DrawColorItem(Color.CYAN),
            DrawColorItem(Color.MAGENTA),
            DrawColorItem(Color.rgb(255, 165, 0)),
            DrawColorItem(Color.rgb(170, 220, 235))
        )
    }
}