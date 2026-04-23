package com.sethy.easypay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.User
import com.sethy.easypay.ui.theme.BrandBlack
import com.sethy.easypay.ui.theme.SurfaceSubtle
import com.sethy.easypay.ui.theme.TextSecondary

enum class BottomNavItem {
    HOME,
    CALENDAR,
    NOTIFICATIONS,
    PROFILE
}

@Composable
fun AppBottomBar(
    selectedTab: BottomNavItem,
    onTabSelected: (BottomNavItem) -> Unit,
    onScanClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Home",
                    isSelected = selectedTab == BottomNavItem.HOME,
                    icon = Lucide.House,
                    onClick = { onTabSelected(BottomNavItem.HOME) }
                )

                NavItem(
                    label = "Calendar",
                    isSelected = selectedTab == BottomNavItem.CALENDAR,
                    icon = Lucide.Calendar,
                    onClick = { onTabSelected(BottomNavItem.CALENDAR) }
                )

                Spacer(modifier = Modifier.width(56.dp))

                NavItem(
                    label = "Alerts",
                    isSelected = selectedTab == BottomNavItem.NOTIFICATIONS,
                    icon = Lucide.Bell,
                    onClick = { onTabSelected(BottomNavItem.NOTIFICATIONS) }
                )

                NavItem(
                    label = "Profile",
                    isSelected = selectedTab == BottomNavItem.PROFILE,
                    icon = Lucide.User,
                    onClick = { onTabSelected(BottomNavItem.PROFILE) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FloatingScanButton(onClick = onScanClicked)
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .width(56.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) SurfaceSubtle else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) BrandBlack else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isSelected) BrandBlack else TextSecondary
        )
    }
}

@Composable
private fun FloatingScanButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(BrandBlack)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Lucide.QrCode,
            contentDescription = "Scan",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
