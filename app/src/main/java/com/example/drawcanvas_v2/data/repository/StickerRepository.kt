package com.example.drawcanvas_v2.data.repository
import android.content.Context
import com.example.drawcanvas_v2.data.model.StickerItem
class StickerRepository {
    fun getStickers(
        context: Context
    ): List<StickerItem> {
        return context
            .assets
            .list("stickers")
            ?.filter {
                it.endsWith(
                    ".png",
                    ignoreCase = true
                )
            }
            ?.map {
                StickerItem(
                    path = "stickers/$it"
                )
            }
            ?: emptyList()
    }
}