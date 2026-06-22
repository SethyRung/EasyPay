package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.*

@Composable
fun CategoryTab(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (active) SurfaceCard else Color.Transparent
    ) {
        Text(
            text = label,
            style = EasyPayTypography.navLink,
            color = if (active) Ink else Muted,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Preview
@Composable
private fun CategoryTabPreview() {
    EasyPayTheme {
        CategoryTab(label = "All", active = true, onClick = {})
        CategoryTab(label = "Alerts", active = false, onClick = {})
    }
}
