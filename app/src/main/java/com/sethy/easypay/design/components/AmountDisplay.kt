package com.sethy.easypay.design.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Ink

@Composable
fun AmountDisplay(
    amount: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = EasyPayTypography.displayMD,
    prefix: String = "$",
    color: Color = Ink
) {
    AnimatedContent(
        targetState = amount,
        transitionSpec = {
            val direction = if (
                targetState.length > initialState.length ||
                (targetState.toDoubleOrNull() ?: 0.0) > (initialState.toDoubleOrNull() ?: 0.0)
            ) {
                slideInVertically { -it } + fadeIn() togetherWith
                    slideOutVertically { it } + fadeOut()
            } else {
                slideInVertically { it } + fadeIn() togetherWith
                    slideOutVertically { -it } + fadeOut()
            }
            direction
        },
        label = "amountAnimation",
        modifier = modifier
    ) { targetAmount ->
        Text(
            text = "$prefix$targetAmount",
            style = textStyle,
            color = color
        )
    }
}

@Preview
@Composable
private fun AmountDisplayPreview() {
    EasyPayTheme {
        AmountDisplay(amount = "1,250.00")
    }
}
