package com.sethy.easypay.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.design.Body
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDarkSoft
import com.sethy.easypay.design.components.AmountDisplay
import com.sethy.easypay.design.components.BadgeCoral
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.ButtonTextLink
import com.sethy.easypay.design.components.ProductMockupCardDark
import com.sethy.easypay.design.components.ProfileInfoCard
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.state.TransactionDetailEffect
import com.sethy.easypay.ui.state.TransactionDetailEvent
import com.sethy.easypay.ui.viewmodel.TransactionDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentState = state

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TransactionDetailEffect.NavigateBack -> onBackClick()
                is TransactionDetailEffect.ShowError -> { /* Handled via state.errorMessage */ }
                TransactionDetailEffect.ShareReceipt -> { /* TODO */ }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Transaction",
                showBackButton = true,
                onBackClick = { viewModel.onEvent(TransactionDetailEvent.Back) }
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
                currentState.transaction != null -> {
                    TransactionDetailContent(
                        transaction = currentState.transaction,
                        viewModel = viewModel
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.errorMessage ?: "Transaction not found",
                            style = EasyPayTypography.bodyMD,
                            color = Muted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailContent(
    transaction: Transaction,
    viewModel: TransactionDetailViewModel
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
    val dateStr = dateFormat.format(Date(transaction.timestamp))

    ProductMockupCardDark(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md),
            modifier = Modifier.fillMaxWidth()
        ) {
            BadgeCoral(
                text = transaction.type.name,
                uppercase = true
            )
            AmountDisplay(
                amount = String.format("%.2f", transaction.amount),
                textStyle = EasyPayTypography.displayLG,
                prefix = "$"
            )
            Text(
                text = dateStr,
                style = EasyPayTypography.caption,
                color = OnDarkSoft
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        InfoRow(label = "Recipient", value = transaction.recipientName)
        InfoRow(label = "Status", value = transaction.status.name)
        InfoRow(label = "Fee", value = "$0.00")
        InfoRow(label = "Reference", value = transaction.id)
        InfoRow(label = "Date", value = dateStr)
    }

    Spacer(modifier = Modifier.height(EasyPaySpacing.md))

    ButtonPrimary(
        text = "Share receipt",
        onClick = { viewModel.onEvent(TransactionDetailEvent.ShareReceipt) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
    ButtonTextLink(
        text = "Report issue",
        onClick = { viewModel.onEvent(TransactionDetailEvent.ReportIssue) }
    )
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

@Preview
@Composable
private fun TransactionDetailScreenPreview() {
    EasyPayTheme {
        TransactionDetailScreen(onBackClick = {})
    }
}
