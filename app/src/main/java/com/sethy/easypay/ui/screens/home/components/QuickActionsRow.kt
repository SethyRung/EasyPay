package com.sethy.easypay.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.ShoppingCart
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted

private data class QuickAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun QuickActionsRow(
    onStoreClick: () -> Unit,
    onSendClick: () -> Unit,
    onTopUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        QuickAction(Lucide.ShoppingCart, "Store", onStoreClick),
        QuickAction(Lucide.Send, "Send", onSendClick),
        QuickAction(Lucide.Plus, "Top up", onTopUpClick)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = EasyPaySpacing.md),
        horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
    ) {
        actions.forEach { action ->
            QuickActionTile(
                action = action,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(EasyPayRadius.lg))
            .clickable(onClick = action.onClick),
        shape = RoundedCornerShape(EasyPayRadius.lg),
        border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyPaySpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = Ink,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = action.label,
                style = EasyPayTypography.bodyMD,
                color = Ink
            )
        }
    }
}

@Preview
@Composable
private fun QuickActionsRowPreview() {
    EasyPayTheme {
        QuickActionsRow(
            onStoreClick = {},
            onSendClick = {},
            onTopUpClick = {}
        )
    }
}
