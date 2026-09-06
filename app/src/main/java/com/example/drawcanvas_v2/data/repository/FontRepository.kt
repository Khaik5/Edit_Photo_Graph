package com.example.drawcanvas_v2.data.repository

import com.example.drawcanvas_v2.R
import com.example.drawcanvas_v2.data.model.FontItem

class FontRepository {

    fun getFonts(): List<FontItem> {

        return listOf(
            FontItem(
                "Algerian",
                R.font.algerian
            ),

            FontItem(
                "Arial Black",
                R.font.arial_black
            ),

            FontItem(
                "Bahnschrift",
                R.font.bahnschrift_bold_condensed
            ),

            FontItem(
                "BPG Arial",
                R.font.bpg_arial_webfont
            ),

            FontItem(
                "DejaVu",
                R.font.dejavu_sans_extra_light_webfont
            ),

            FontItem(
                "Bit Neon",
                R.font.mg_bitneon_webfont
            ),

            FontItem(
                "Times",
                R.font.times_new_roman
            )
        )
    }
}