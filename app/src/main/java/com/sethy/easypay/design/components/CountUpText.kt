package com.sethy.easypay.design.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Ink

@Composable
fun CountUpText(
    targetValue: Double,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = EasyPayTypography.displayMD,
    color: Color = Ink,
    durationMillis: Int = 800,
    format: (Double) -> String = { it.toString() }
) {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        label = "countUp"
    )
    Text(
        text = format(animatedValue.toDouble()),
        style = textStyle,
        color = color,
        modifier = modifier
    )
}

@Preview
@Composable
private fun CountUpTextPreview() {
    EasyPayTheme {
        CountUpText(
            targetValue = 1234.56,
            format = { String.format("$%.2f", it) }
        )
    }
}
