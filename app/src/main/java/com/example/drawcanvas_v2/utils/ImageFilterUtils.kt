package com.example.drawcanvas_v2.utils

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentState
import com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentType
import com.example.drawcanvas_v2.ui.canvas_edit.filter.FilterType

object ImageFilterUtils {

    fun create(
        adjustment: AdjustmentState,
        filter: FilterType
    ): ColorMatrixColorFilter {
        val matrix = ColorMatrix()
        applyBrightness(matrix, adjustment.brightness)
        applyContrast(matrix, adjustment.contrast)
        applySaturation(matrix, adjustment.saturation)
        applyHue(matrix, adjustment.hue)
        applyFilter(matrix, filter)
        return ColorMatrixColorFilter(matrix)
    }

    private fun applyBrightness(
        matrix: ColorMatrix,
        value: Int // -100 100
    ) {
        // offset để dịch chuyển
        val offset = value * 2.55f // vì giá trị lớn nhất là 100, cộng nhiều nhất là 255 vì màu tối đa là 255
        matrix.postConcat(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, offset,
                    0f, 1f, 0f, 0f, offset,
                    0f, 0f, 1f, 0f, offset,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )
    }
    private fun applyContrast(
        matrix: ColorMatrix,
        value: Int // -100 100
    ) {
        // khi value đc kéo thì contrast thay đổi theo max = 2 min = 0  avg =1
        // cộng 1 để giữa nguyên hình ảnh không thì nó sẽ ra màu đen
        val contrast = 1f + value / 100f // khi value 0 thì contrast không đổi,
        val translate = 128f - contrast * 128f // 0 đen đến 255(trắng) 255/2 xám bth
        matrix.postConcat(
            ColorMatrix(
                floatArrayOf(
                    contrast, 0f, 0f, 0f, translate,
                    0f, contrast, 0f, 0f, translate,
                    0f, 0f, contrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )
    }

    private fun applySaturation(
        matrix: ColorMatrix,
        value: Int
    ) {

        val saturation = 1f + value / 100f

        val saturationMatrix = ColorMatrix() // bảng màu mới
        saturationMatrix.setSaturation(saturation) // dùng để giữ nguyên độ sách của ảnh, điều chỉnh hệ số bảo hòa theo satutration
        matrix.postConcat(saturationMatrix) // apps dụng saturation
    }

    private fun applyHue(
        matrix: ColorMatrix,
        value: Int
    ) {

        if (value == 0) {
            return
        }

        val radians =
            Math.toRadians(
                value.toDouble()
            )

        val cos =
            kotlin.math.cos(
                radians
            ).toFloat()

        val sin =
            kotlin.math.sin(
                radians
            ).toFloat()
        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f
        val hueMatrix =
            ColorMatrix(
                floatArrayOf(
                    lumR + cos * (1f - lumR) + sin * -lumR,
                    lumG + cos * -lumG + sin * -lumG,
                    lumB + cos * -lumB + sin * (1f - lumB),
                    0f,
                    0f,

                    lumR + cos * -lumR +
                            sin * 0.143f,

                    lumG + cos * (1f - lumG) +
                            sin * 0.140f,

                    lumB + cos * -lumB +
                            sin * -0.283f,

                    0f,
                    0f,

                    lumR + cos * -lumR +
                            sin * -(1f - lumR),

                    lumG + cos * -lumG +
                            sin * lumG,

                    lumB + cos * (1f - lumB) +
                            sin * lumB,

                    0f,
                    0f,

                    0f,
                    0f,
                    0f,
                    1f,
                    0f
                )
            )

        matrix.postConcat(
            hueMatrix
        )
    }

    private fun applyFilter(
        matrix: ColorMatrix,
        filter: FilterType
    ) {
        when (filter) {
            FilterType.NONE -> Unit
            FilterType.SEPIA -> {
                matrix.postConcat(
                    ColorMatrix(
                        floatArrayOf(
                            0.393f, 0.769f, 0.189f, 0f, 0f,
                            0.349f, 0.686f, 0.168f, 0f, 0f,
                            0.272f, 0.534f, 0.131f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            }

            FilterType.INVERT -> {
                matrix.postConcat(
                    ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            }

            FilterType.NOISE -> Unit
        }
    }
}