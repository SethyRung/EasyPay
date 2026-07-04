package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wallet
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.Ink

@Composable
fun BrandMark(
    size: Dp,
    modifier: Modifier = Modifier,
    tint: Color = Ink
) {
    Icon(
        imageVector = Lucide.Wallet,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(size)
    )
}

@Preview
@Composable
private fun BrandMarkPreview() {
    EasyPayTheme {
        BrandMark(size = 24.dp)
    }
}