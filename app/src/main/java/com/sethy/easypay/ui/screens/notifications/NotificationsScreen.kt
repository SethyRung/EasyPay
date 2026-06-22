package com.sethy.easypay.ui.screens.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.model.NotificationType
import com.sethy.easypay.design.AccentAmber
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.Success
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.state.NotificationTab
import com.sethy.easypay.ui.state.NotificationsEffect
import com.sethy.easypay.ui.state.NotificationsEvent
import com.sethy.easypay.ui.viewmodel.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NotificationsEffect.NavigateBack -> onBackClick()
                is NotificationsEffect.ShowError -> { /* Handled via state.errorMessage */ }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Notifications",
                showBackButton = true,
                onBackClick = { viewModel.onEvent(NotificationsEvent.Back) }
            )
        },
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl)
        ) {
            NotificationTabs(
                selectedTab = state.selectedTab,
                onTabSelected = { viewModel.onEvent(NotificationsEvent.TabSelected(it)) }
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.errorMessage.orEmpty(),
                            style = EasyPayTypography.bodyMD,
                            color = Muted
                        )
                    }
                }
                else -> {
                    val filtered = filterNotifications(state.notifications, state.selectedTab)
                    if (filtered.isEmpty()) {
                        EmptyNotifications()
                    } else {
                        NotificationList(
                            notifications = filtered,
                            onNotificationClick = { id ->
                                viewModel.onEvent(NotificationsEvent.NotificationClicked(id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationTabs(
    selectedTab: NotificationTab,
    onTabSelected: (NotificationTab) -> Unit
) {
    val tabs = NotificationTab.entries
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = Canvas,
        contentColor = Ink,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
                    .height(4.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Primary)
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTab.ordinal == index,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.name.lowercase().replaceFirstChar { it.titlecase() },
                        style = EasyPayTypography.button
                    )
                }
            )
        }
    }
}

@Composable
private fun NotificationList(
    notifications: List<Notification>,
    onNotificationClick: (String) -> Unit
) {
    val grouped = notifications.groupByDate()
    val dateFormat = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
    ) {
        grouped.forEach { (dateMillis, items) ->
            item {
                Text(
                    text = dateFormat.format(Date(dateMillis)),
                    style = EasyPayTypography.captionUppercase,
                    color = Muted,
                    modifier = Modifier.padding(vertical = EasyPaySpacing.sm)
                )
            }
            items(items, key = { it.id }) { notification ->
                NotificationItem(
                    notification = notification,
                    timeFormat = timeFormat,
                    onClick = { onNotificationClick(notification.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    timeFormat: SimpleDateFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dotColor = when (notification.type) {
        NotificationType.ALERT -> Error
        NotificationType.RECEIPT -> Success
        NotificationType.PROMO -> AccentAmber
        NotificationType.INFO -> Primary
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Canvas),
        border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyPaySpacing.lg),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = EasyPaySpacing.xs)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(EasyPaySpacing.md))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.xs)
            ) {
                Text(
                    text = notification.title,
                    style = EasyPayTypography.bodyMD.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = Ink
                )
                Text(
                    text = notification.body,
                    style = EasyPayTypography.bodySM,
                    color = Muted
                )
                Text(
                    text = timeFormat.format(Date(notification.timestamp)),
                    style = EasyPayTypography.caption,
                    color = Muted
                )
            }
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Primary)
                )
            }
        }
    }
}

@Composable
private fun EmptyNotifications() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "All caught up",
            style = EasyPayTypography.displaySM,
            color = Ink
        )
        Spacer(modifier = Modifier.height(EasyPaySpacing.sm))
        Text(
            text = "You're up to date",
            style = EasyPayTypography.bodyMD,
            color = Muted
        )
    }
}

private fun filterNotifications(
    notifications: List<Notification>,
    tab: NotificationTab
): List<Notification> = when (tab) {
    NotificationTab.ALL -> notifications
    NotificationTab.UNREAD -> notifications.filter { !it.isRead }
    NotificationTab.ALERTS -> notifications.filter { it.type == NotificationType.ALERT }
}

private fun List<Notification>.groupByDate(): Map<Long, List<Notification>> {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return groupBy { dateFormat.format(Date(it.timestamp)).toLong() }
        .toSortedMap(reverseOrder())
}

@Preview
@Composable
private fun NotificationsScreenPreview() {
    EasyPayTheme {
        NotificationsScreen(onBackClick = {})
    }
}
