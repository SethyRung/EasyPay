package com.sethy.easypay.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.User
import com.sethy.easypay.design.*

enum class BottomNavItem {
    Home, Store, Notifications, Profile
}

private data class BottomNavEntry(
    val item: BottomNavItem,
    val icon: ImageVector,
    val label: String
)

private val bottomNavItems = listOf(
    BottomNavEntry(BottomNavItem.Home, Lucide.House, "Home"),
    BottomNavEntry(BottomNavItem.Store, Lucide.ShoppingCart, "Store"),
    BottomNavEntry(BottomNavItem.Notifications, Lucide.Bell, "Alerts"),
    BottomNavEntry(BottomNavItem.Profile, Lucide.User, "Profile")
)

@Composable
fun BottomNav(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(EasyPayDimens.bottomNavHeight),
        color = Canvas,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = EasyPaySpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { entry ->
                BottomNavSlot(
                    entry = entry,
                    selected = selectedItem == entry.item,
                    onClick = { onItemSelected(entry.item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavSlot(
    entry: BottomNavEntry,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) SurfaceCard else Color.Transparent
    Box(
        modifier = modifier
            .padding(vertical = EasyPaySpacing.sm, horizontal = EasyPaySpacing.xs)
            .clip(RoundedCornerShape(EasyPayRadius.pill))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = EasyPaySpacing.sm, vertical = EasyPaySpacing.xs),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = entry.icon,
                contentDescription = entry.label,
                tint = if (selected) Ink else Muted,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = entry.label,
                style = EasyPayTypography.caption,
                color = if (selected) Ink else Muted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun BottomNavPreview() {
    EasyPayTheme {
        BottomNav(
            selectedItem = BottomNavItem.Home,
            onItemSelected = {}
        )
    }
}