package com.sethy.easypay.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.User

enum class BottomNavItem {
    HOME,
    CALENDAR,
    SCAN,
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
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    isSelected = selectedTab == BottomNavItem.HOME,
                    iconSelected = Lucide.House,
                    iconUnselected = Lucide.House,
                    onClick = { onTabSelected(BottomNavItem.HOME) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                NavItem(
                    isSelected = selectedTab == BottomNavItem.CALENDAR,
                    iconSelected = Lucide.Calendar,
                    iconUnselected = Lucide.Calendar,
                    onClick = { onTabSelected(BottomNavItem.CALENDAR) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Spacer(modifier = Modifier.width(56.dp))

                Spacer(modifier = Modifier.width(8.dp))

                NavItem(
                    isSelected = selectedTab == BottomNavItem.NOTIFICATIONS,
                    iconSelected = Lucide.Bell,
                    iconUnselected = Lucide.Bell,
                    onClick = { onTabSelected(BottomNavItem.NOTIFICATIONS) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                NavItem(
                    isSelected = selectedTab == BottomNavItem.PROFILE,
                    iconSelected = Lucide.User,
                    iconUnselected = Lucide.User,
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
    isSelected: Boolean,
    iconSelected: androidx.compose.ui.graphics.vector.ImageVector,
    iconUnselected: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = null,
            tint = if (isSelected) Color.Black else Color.Gray,
            modifier = Modifier.size(24.dp)
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
            .background(Color.Black)
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
