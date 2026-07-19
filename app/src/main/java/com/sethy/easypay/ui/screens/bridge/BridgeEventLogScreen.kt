package com.sethy.easypay.ui.screens.bridge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Loader
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.sethy.easypay.bridge.BridgeEvent
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDark
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.Success
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.viewmodel.BridgeEventLogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BridgeEventLogScreen(
    viewModel: BridgeEventLogViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Bridge events",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = Canvas
    ) { padding ->
        if (events.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(EasyPaySpacing.xl),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No bridge events yet",
                    style = EasyPayTypography.titleMD,
                    color = Ink
                )
                Text(
                    text = "Open the Glitch Store to start a session",
                    style = EasyPayTypography.bodyMD,
                    color = Muted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = EasyPaySpacing.md),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(
                    items = events.asReversed(),
                    key = { it.id }
                ) { event ->
                    EventRow(event = event, timeFormat = timeFormat)
                    androidx.compose.material3.HorizontalDivider(color = Hairline)
                }
            }
        }
    }
}

@Composable
private fun EventRow(event: BridgeEvent, timeFormat: SimpleDateFormat) {
    val (icon, tint, statusText) = when (event) {
        is BridgeEvent.Received -> Triple(Lucide.Loader, Primary, "called")
        is BridgeEvent.Replied -> Triple(Lucide.Check, Success, if (event.ok) "succeeded" else "failed")
        is BridgeEvent.Failed -> Triple(Lucide.TriangleAlert, com.sethy.easypay.design.Error, "error")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = EasyPaySpacing.xl, vertical = EasyPaySpacing.md),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.size(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconDot(icon = icon, tint = tint)
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(EasyPaySpacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.method,
                    style = EasyPayTypography.bodyMD.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = timeFormat.format(Date(event.timestampMillis)),
                    style = EasyPayTypography.caption,
                    color = Muted
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = statusText,
                style = EasyPayTypography.caption,
                color = tint
            )
            if (event is BridgeEvent.Received && event.payload != null) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = event.payload,
                    style = EasyPayTypography.caption,
                    color = Muted,
                    maxLines = 2
                )
            }
            if (event is BridgeEvent.Failed) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = event.reason,
                    style = EasyPayTypography.caption,
                    color = com.sethy.easypay.design.Error,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun IconDot(icon: ImageVector, tint: Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview
@Composable
private fun BridgeEventLogScreenPreview() {
    EasyPayTheme {
        BridgeEventLogScreen(onBackClick = {})
    }
}