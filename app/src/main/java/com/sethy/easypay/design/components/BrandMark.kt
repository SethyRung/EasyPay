package com.sethy.easypay.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.Ink
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BrandMark(
    size: Dp,
    modifier: Modifier = Modifier,
    tint: Color = Ink,
    strokeWidth: Dp = size / 6
) {
    Canvas(modifier = modifier.size(size)) {
        val center = size.toPx() / 2
        val length = center * 0.75f
        val gap = center * 0.25f
        val stroke = strokeWidth.toPx()

        listOf(0f, 90f, 180f, 270f).forEach { angle ->
            val rad = Math.toRadians(angle.toDouble()).toFloat()
            val startX = center + cos(rad) * gap
            val startY = center + sin(rad) * gap
            val endX = center + cos(rad) * length
            val endY = center + sin(rad) * length
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset(endX, endY),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
private fun BrandMarkPreview() {
    EasyPayTheme {
        BrandMark(size = 48.dp)
    }
}
