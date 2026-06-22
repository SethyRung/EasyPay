package com.sethy.easypay.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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

private val bottomNavItems = listOf(
    BottomNavItem.Home to (Lucide.House to "Home"),
    BottomNavItem.Calendar to (Lucide.Calendar to "Calendar"),
    BottomNavItem.Notifications to (Lucide.Bell to "Alerts"),
    BottomNavItem.Profile to (Lucide.User to "Profile")
)

private val BottomNavItem.icon: ImageVector
    get() = bottomNavItems.first { it.first == this }.second.first

private val BottomNavItem.label: String
    get() = bottomNavItems.first { it.first == this }.second.second

@Composable
fun BottomNav(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = Canvas,
            tonalElevation = 0.dp,
            modifier = Modifier.height(EasyPayDimens.bottomNavHeight)
        ) {
            bottomNavItems.forEach { (item, _) ->
                NavigationBarItem(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                    label = { Text(text = item.label, style = EasyPayTypography.caption) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Ink,
                        selectedTextColor = Ink,
                        indicatorColor = SurfaceCard,
                        unselectedIconColor = Muted,
                        unselectedTextColor = Muted
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(bottom = (EasyPayDimens.bottomNavHeight - 56.dp) / 2)
                .size(56.dp)
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
