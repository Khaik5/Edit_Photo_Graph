package com.example.drawcanvas_v2.data.repository

import android.content.Context
import com.example.drawcanvas_v2.data.model.FrameItem

class FrameRepository {

    fun getFrames(
        context: Context
    ): List<FrameItem> {

        return context.assets
            .list("frame")
            ?.filter {
                it.endsWith(
                    ".png",
                    ignoreCase = true
                )
            }
            ?.sorted()
            ?.map {
                FrameItem(
                    "frame/$it"
                )
            }
            ?: emptyList()
    }
}