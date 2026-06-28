package com.sethy.easypay.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.Droplet
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Tv
import com.composables.icons.lucide.Zap
import com.sethy.easypay.design.AccentAmber
import com.sethy.easypay.design.AccentTeal
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDark
import com.sethy.easypay.design.OnDarkSoft
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.components.BadgePill
import com.sethy.easypay.design.components.ButtonSecondaryOnDark
import com.sethy.easypay.design.components.CalloutCardCoral
import com.sethy.easypay.design.components.ConnectorTile
import com.sethy.easypay.design.components.CountUpText
import com.sethy.easypay.design.components.ProductMockupCardDark
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.components.TransactionItem
import com.sethy.easypay.ui.state.HomeEffect
import com.sethy.easypay.ui.state.HomeEvent
import com.sethy.easypay.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSendMoney: () -> Unit,
    onNavigateToTransactionDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToSendMoney -> onNavigateToSendMoney()
                is HomeEffect.NavigateToTransactionDetail -> onNavigateToTransactionDetail(effect.id)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopNav(title = "EasyPay") },
        containerColor = Canvas
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl),
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.lg),
            contentPadding = PaddingValues(vertical = EasyPaySpacing.lg)
        ) {
            item { BalanceHero(state.balance, viewModel::onEvent) }
            item { PromoCard() }
            item { BadgePill(text = "Quick Actions") }
            item { QuickActionsGrid(viewModel::onEvent) }
            item {
                Text(
                    text = "Recent transactions",
                    style = EasyPayTypography.titleMD,
                    color = Ink
                )
            }
            items(
                items = state.transactions,
                key = { it.id }
            ) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { viewModel.onEvent(HomeEvent.TransactionClick(transaction.id)) },
                    modifier = Modifier.animateItem()
                )
            }

            state.errorMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        style = EasyPayTypography.caption,
                        color = com.sethy.easypay.design.Error
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceHero(
    balance: Double,
    onEvent: (HomeEvent) -> Unit
) {
    ProductMockupCardDark(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Available balance",
            style = EasyPayTypography.caption,
            color = OnDarkSoft
        )
        Spacer(modifier = Modifier.height(EasyPaySpacing.xs))
        CountUpText(
            targetValue = balance,
            textStyle = EasyPayTypography.displayMD,
            color = OnDark,
            format = { String.format("$%.2f", it) }
        )
        Spacer(modifier = Modifier.height(EasyPaySpacing.lg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
        ) {
            ButtonSecondaryOnDark(
                text = "Top up",
                onClick = { },
                modifier = Modifier.weight(1f)
            )
            ButtonSecondaryOnDark(
                text = "Send",
                onClick = { onEvent(HomeEvent.SendMoneyClick) },
                modifier = Modifier.weight(1f)
            )
            ButtonSecondaryOnDark(
                text = "Withdraw",
                onClick = { },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PromoCard() {
    CalloutCardCoral(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)) {
            Text(
                text = "30% off your first transfer",
                style = EasyPayTypography.titleMD,
                color = com.sethy.easypay.design.OnPrimary
            )
            Text(
                text = "Send money to friends and family with zero fees on your first transfer.",
                style = EasyPayTypography.bodyMD,
                color = com.sethy.easypay.design.OnPrimary
            )
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Canvas,
                    contentColor = Primary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Send now",
                    style = EasyPayTypography.button,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(onEvent: (HomeEvent) -> Unit) {
    val rows = listOf(
        listOf(
            Triple(Lucide.Globe, "Internet", AccentTeal),
            Triple(Lucide.Droplet, "Water", AccentAmber),
            Triple(Lucide.Zap, "Electricity", com.sethy.easypay.design.Warning),
            Triple(Lucide.Tv, "TV Cable", com.sethy.easypay.design.Success)
        ),
        listOf(
            Triple(Lucide.Car, "Vehicle", com.sethy.easypay.design.Error),
            Triple(Lucide.House, "Rent", AccentTeal),
            Triple(Lucide.Landmark, "Invest", AccentAmber),
            Triple(Lucide.LayoutGrid, "More", Muted)
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md),
        modifier = Modifier.fillMaxWidth()
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
            ) {
                row.forEach { (icon, label, tint) ->
                    ConnectorTile(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(tint.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = tint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(EasyPaySpacing.sm))
                        Text(
                            text = label,
                            style = EasyPayTypography.titleSM,
                            color = Ink
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    EasyPayTheme {
        HomeScreen(
            onNavigateToSendMoney = {},
            onNavigateToTransactionDetail = {}
        )
    }
}