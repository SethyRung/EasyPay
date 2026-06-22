package com.sethy.easypay.ui.screens.send

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.sethy.easypay.design.Body
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.SurfaceCard
import com.sethy.easypay.design.components.AmountDisplay
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.SendAmountKeypad
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.state.SendMoneyEffect
import com.sethy.easypay.ui.state.SendMoneyEvent
import com.sethy.easypay.ui.viewmodel.SendMoneyViewModel

@Composable
fun SendMoneyScreen(
    recipientName: String,
    viewModel: SendMoneyViewModel = hiltViewModel(),
    onNavigateToTransferSuccess: (String, Double) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SendMoneyEffect.NavigateToTransferSuccess -> {
                    onNavigateToTransferSuccess(effect.recipient, effect.amount)
                }
                SendMoneyEffect.NavigateBack -> onBackClick()
                is SendMoneyEffect.ShowError -> { /* Handled via state.errorMessage */ }
            }
        }
    }

    val amountValue = state.amount.toDoubleOrNull() ?: 0.0

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Send Money",
                showBackButton = true,
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search",
                            tint = Ink
                        )
                    }
                }
            )
        },
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.lg, Alignment.CenterVertically)
        ) {
            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

            RecipientAvatar(recipientName)

            Text(
                text = recipientName,
                style = EasyPayTypography.titleMD,
                color = Ink,
                textAlign = TextAlign.Center
            )
            Text(
                text = "+91 8050530XXX",
                style = EasyPayTypography.bodyMD,
                color = Muted,
                textAlign = TextAlign.Center
            )

            AmountDisplay(
                amount = state.amount,
                textStyle = EasyPayTypography.displayMD,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(color = Hairline)

            SendAmountKeypad(
                onKeyClick = { viewModel.onEvent(SendMoneyEvent.KeypadKeyClicked(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            state.errorMessage?.let {
                Text(
                    text = it,
                    style = EasyPayTypography.caption,
                    color = com.sethy.easypay.design.Error
                )
            }

            ButtonPrimary(
                text = "Send",
                onClick = {
                    viewModel.onEvent(SendMoneyEvent.Send(recipientName, amountValue))
                },
                enabled = !state.isLoading && amountValue > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))
        }
    }
}

@Composable
private fun RecipientAvatar(recipientName: String) {
    val initials = recipientName
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
            style = EasyPayTypography.displaySM,
            color = Body
        )
    }
}

@Preview
@Composable
private fun SendMoneyScreenPreview() {
    EasyPayTheme {
        SendMoneyScreen(
            recipientName = "Nayantara V",
            onNavigateToTransferSuccess = { _, _ -> },
            onBackClick = {}
        )
    }
}
