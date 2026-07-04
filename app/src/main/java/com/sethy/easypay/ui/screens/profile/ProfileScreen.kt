package com.sethy.easypay.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.sethy.easypay.bridge.BridgeStatus
import com.sethy.easypay.data.model.User
import com.sethy.easypay.design.Body
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDark
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.Success
import com.sethy.easypay.design.SurfaceCard
import com.sethy.easypay.design.components.ButtonSecondary
import com.sethy.easypay.design.components.ProfileInfoCard
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.state.ProfileEffect
import com.sethy.easypay.ui.state.ProfileEvent
import com.sethy.easypay.ui.viewmodel.BridgeSessionViewModel
import com.sethy.easypay.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    bridgeSessionViewModel: BridgeSessionViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToBridgeEventLog: () -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentState = state
    val bridgeSession by bridgeSessionViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.NavigateToLogin -> onNavigateToLogin()
                ProfileEffect.NavigateBack -> onBackClick()
                is ProfileEffect.ShowError -> { /* Handled via state.errorMessage */ }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Profile",
                showBackButton = true,
                onBackClick = { viewModel.onEvent(ProfileEvent.Back) }
            )
        },
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.lg)
        ) {
            when {
                currentState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                currentState.user != null -> {
                    ProfileContent(
                        user = currentState.user,
                        viewModel = viewModel,
                        bridgeSession = bridgeSession,
                        onNavigateToBridgeEventLog = onNavigateToBridgeEventLog,
                        errorMessage = currentState.errorMessage
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.errorMessage ?: "Profile unavailable",
                            style = EasyPayTypography.bodyMD,
                            color = Muted
                        )
                    }
                }
            }
        }
    }

    if (currentState.showLogoutDialog) {
        LogoutDialog(
            onConfirm = { viewModel.onEvent(ProfileEvent.ConfirmLogout) },
            onDismiss = { viewModel.onEvent(ProfileEvent.DismissLogout) }
        )
    }
}

@Composable
private fun ProfileContent(
    user: User,
    viewModel: ProfileViewModel,
    bridgeSession: com.sethy.easypay.ui.viewmodel.BridgeSessionUiState,
    onNavigateToBridgeEventLog: () -> Unit,
    errorMessage: String?
) {
    Spacer(modifier = Modifier.height(EasyPaySpacing.md))
    ProfileAvatar(user.name)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.xs)
    ) {
        Text(
            text = user.name,
            style = EasyPayTypography.titleLG,
            color = Ink,
            textAlign = TextAlign.Center
        )
        Text(
            text = user.email,
            style = EasyPayTypography.bodyMD,
            color = Muted,
            textAlign = TextAlign.Center
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        user.phone?.takeIf { it.isNotBlank() }?.let { phone ->
            InfoRow(label = "Phone", value = phone)
        }
        InfoRow(label = "Account ID", value = user.id)
        InfoRow(label = "Currency", value = "USD")
    }

    BridgeSessionCard(
        state = bridgeSession,
        onViewLog = onNavigateToBridgeEventLog
    )

    if (!errorMessage.isNullOrBlank()) {
        Text(
            text = errorMessage,
            style = EasyPayTypography.caption,
            color = Error
        )
    }

    Spacer(modifier = Modifier.height(EasyPaySpacing.sm))

    ButtonSecondary(
        text = "Edit profile",
        onClick = { viewModel.onEvent(ProfileEvent.EditProfile) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )

    Button(
        onClick = { viewModel.onEvent(ProfileEvent.LogoutClicked) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Error.copy(alpha = 0.12f),
            contentColor = Error
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "Log out",
            style = EasyPayTypography.button
        )
    }

    Spacer(modifier = Modifier.height(EasyPaySpacing.lg))
}

@Composable
private fun BridgeSessionCard(
    state: com.sethy.easypay.ui.viewmodel.BridgeSessionUiState,
    onViewLog: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Bridge session",
            style = EasyPayTypography.captionUppercase,
            color = Muted,
            modifier = Modifier.padding(start = EasyPaySpacing.xs)
        )
        Spacer(modifier = Modifier.height(EasyPaySpacing.xs))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(EasyPayRadius.lg),
            border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
            color = Canvas
        ) {
            Column(modifier = Modifier.padding(EasyPaySpacing.md)) {
                SessionInfoRow(
                    label = "Connected merchant",
                    value = state.merchant,
                    valueColor = if (state.isActive) Success else Muted
                )
                Spacer(modifier = Modifier.height(EasyPaySpacing.sm))
                SessionInfoRow(
                    label = "Session started",
                    value = formatSessionAge(state.sessionStartedAtMillis)
                )
                Spacer(modifier = Modifier.height(EasyPaySpacing.sm))
                SessionInfoRow(
                    label = "Events",
                    value = state.eventCount.toString()
                )

                Spacer(modifier = Modifier.height(EasyPaySpacing.md))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(EasyPayRadius.md))
                        .clickable(enabled = state.eventCount > 0, onClick = onViewLog),
                    color = if (state.eventCount > 0) Primary.copy(alpha = 0.08f) else Canvas,
                    shape = RoundedCornerShape(EasyPayRadius.md),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Hairline)
                ) {
                    Row(
                        modifier = Modifier.padding(EasyPaySpacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (state.eventCount > 0) "View bridge event log" else "No events yet",
                            style = EasyPayTypography.bodyMD.copy(
                                fontWeight = if (state.eventCount > 0) FontWeight.Medium else FontWeight.Normal
                            ),
                            color = if (state.eventCount > 0) Primary else Muted
                        )
                        if (state.eventCount > 0) {
                            Icon(
                                imageVector = Lucide.ChevronRight,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionInfoRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = Ink) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = EasyPayTypography.caption,
            color = Muted
        )
        Text(
            text = value,
            style = EasyPayTypography.bodyMD.copy(fontWeight = FontWeight.Medium),
            color = valueColor
        )
    }
}

private fun formatSessionAge(startedAtMillis: Long?): String {
    if (startedAtMillis == null) return "—"
    val diffMs = (System.currentTimeMillis() - startedAtMillis).coerceAtLeast(0)
    val totalSeconds = diffMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return when {
        minutes >= 60 -> "${minutes / 60}h ${minutes % 60}m ago"
        minutes > 0 -> "${minutes}m ${seconds}s ago"
        else -> "${seconds}s ago"
    }
}

@Composable
private fun ProfileAvatar(name: String) {
    val initials = name
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(SurfaceCard),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = EasyPayTypography.displayMD,
            color = Body
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    ProfileInfoCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.xs)) {
            Text(
                text = label,
                style = EasyPayTypography.caption,
                color = Body
            )
            Text(
                text = value,
                style = EasyPayTypography.bodyMD,
                color = Ink
            )
        }
    }
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log out?",
                style = EasyPayTypography.titleMD,
                color = Ink
            )
        },
        text = {
            Text(
                text = "You'll need to sign in again to access your wallet.",
                style = EasyPayTypography.bodyMD,
                color = Muted
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Log out",
                    style = EasyPayTypography.button,
                    color = Error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = EasyPayTypography.button,
                    color = Ink
                )
            }
        }
    )
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    EasyPayTheme {
        ProfileScreen(
            onNavigateToLogin = {},
            onBackClick = {}
        )
    }
}