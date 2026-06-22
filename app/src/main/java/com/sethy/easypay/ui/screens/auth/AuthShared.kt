package com.sethy.easypay.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted

@Composable
internal fun OrDivider(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Hairline
        )
        Text(
            text = text,
            style = EasyPayTypography.bodySM,
            color = Muted,
            modifier = Modifier.padding(horizontal = EasyPaySpacing.md)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Hairline
        )
    }
}

@Composable
internal fun SocialButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(EasyPayRadius.lg),
        border = BorderStroke(1.dp, Hairline),
        contentPadding = PaddingValues(horizontal = EasyPaySpacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
        ) {
            icon()
            Text(text = text, style = EasyPayTypography.titleSM, color = Ink)
        }
    }
}