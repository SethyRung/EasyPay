package com.sethy.easypay.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sethy.easypay.data.model.User
import com.sethy.easypay.design.Body
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.SurfaceCard
import com.sethy.easypay.design.components.ButtonSecondary
import com.sethy.easypay.design.components.ProfileInfoCard
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.state.ProfileEffect
import com.sethy.easypay.ui.state.ProfileEvent
import com.sethy.easypay.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentState = state

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.NavigateToOnboarding -> onNavigateToOnboarding()
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
            onNavigateToOnboarding = {},
            onBackClick = {}
        )
    }
}