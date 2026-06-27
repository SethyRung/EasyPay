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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.User
import com.sethy.easypay.design.*

enum class BottomNavItem {
    Home, Calendar, Notifications, Profile
}

private data class BottomNavEntry(
    val item: BottomNavItem,
    val icon: ImageVector,
    val label: String
)

private val bottomNavItems = listOf(
    BottomNavEntry(BottomNavItem.Home, Lucide.House, "Home"),
    BottomNavEntry(BottomNavItem.Calendar, Lucide.Calendar, "Calendar"),
    BottomNavEntry(BottomNavItem.Notifications, Lucide.Bell, "Alerts"),
    BottomNavEntry(BottomNavItem.Profile, Lucide.User, "Profile")
)

private val ScanButtonSize = 56.dp
private val ScanSpacerWidth = ScanButtonSize + EasyPaySpacing.md * 2

@Composable
fun BottomNav(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
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
                bottomNavItems.take(2).forEach { entry ->
                    BottomNavSlot(
                        entry = entry,
                        selected = selectedItem == entry.item,
                        onClick = { onItemSelected(entry.item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.width(ScanSpacerWidth))
                bottomNavItems.drop(2).forEach { entry ->
                    BottomNavSlot(
                        entry = entry,
                        selected = selectedItem == entry.item,
                        onClick = { onItemSelected(entry.item) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-12).dp)
                .size(ScanButtonSize)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = SurfaceDark,
                    spotColor = SurfaceDark
                )
                .clip(CircleShape)
                .background(SurfaceDark)
                .clickable(onClick = onScanClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.QrCode,
                contentDescription = "Scan",
                tint = OnDark,
                modifier = Modifier.size(28.dp)
            )
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
            onItemSelected = {},
            onScanClick = {}
        )
    }
}