package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Ink

@Composable
fun EasyPayWordmark(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrandMark(size = 24.dp, tint = Ink)
        Spacer(modifier = Modifier.width(EasyPaySpacing.sm))
        Text(text = "EasyPay", style = EasyPayTypography.titleMD, color = Ink)
    }
}

@Preview
@Composable
private fun WordmarkPreview() {
    EasyPayTheme {
        EasyPayWordmark()
    }
}
