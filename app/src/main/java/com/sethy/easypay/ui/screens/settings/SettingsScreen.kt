package com.sethy.easypay.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.components.TopNav

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToPaymentMethods: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Settings",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl),
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
        ) {
            Spacer(modifier = Modifier.height(EasyPaySpacing.md))
            Text(
                text = "Account",
                style = EasyPayTypography.captionUppercase,
                color = Muted
            )
            SettingsRow(
                icon = Lucide.User,
                title = "Edit profile",
                onClick = onNavigateToEditProfile
            )
            Spacer(modifier = Modifier.height(EasyPaySpacing.md))
            Text(
                text = "Activity",
                style = EasyPayTypography.captionUppercase,
                color = Muted
            )
            SettingsRow(
                icon = Lucide.Calendar,
                title = "Calendar",
                onClick = onNavigateToCalendar
            )
            SettingsRow(
                icon = Lucide.CreditCard,
                title = "Payment methods",
                onClick = onNavigateToPaymentMethods
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(EasyPayRadius.lg),
        color = Canvas,
        border = BorderStroke(1.dp, Hairline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyPaySpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Muted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(EasyPaySpacing.md))
            Text(
                text = title,
                style = EasyPayTypography.bodyMD.copy(fontWeight = FontWeight.Medium),
                color = Ink,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = Muted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    EasyPayTheme {
        SettingsScreen(onBackClick = {})
    }
}
