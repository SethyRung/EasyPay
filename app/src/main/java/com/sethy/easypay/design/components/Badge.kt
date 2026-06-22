package com.sethy.easypay.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.*

@Composable
fun BadgePill(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    AssistChip(
        onClick = onClick ?: {},
        label = { Text(text = text, style = EasyPayTypography.caption) },
        modifier = modifier,
        shape = CircleShape,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = SurfaceCard,
            labelColor = Ink
        ),
        border = null
    )
}

@Composable
fun BadgeCoral(
    text: String,
    modifier: Modifier = Modifier,
    uppercase: Boolean = false,
    statusDotColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    AssistChip(
        onClick = onClick ?: {},
        label = {
            Text(
                text = if (uppercase) text.uppercase() else text,
                style = if (uppercase) EasyPayTypography.captionUppercase else EasyPayTypography.caption
            )
        },
        modifier = modifier,
        shape = CircleShape,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Primary,
            labelColor = OnPrimary
        ),
        border = null,
        leadingIcon = statusDotColor?.let { color ->
            {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    )
}

@Preview
@Composable
private fun BadgePreview() {
    EasyPayTheme {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BadgePill(text = "Quick Actions")
            Spacer(modifier = Modifier.width(EasyPaySpacing.sm))
            BadgeCoral(text = "New", uppercase = true, statusDotColor = Success)
        }
    }
}
