package com.sethy.easypay.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import com.sethy.easypay.data.model.FeaturedGame
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDark
import com.sethy.easypay.design.OnDarkSoft
import com.sethy.easypay.design.SurfaceDarkElevated
import com.sethy.easypay.design.components.CountUpText
import com.sethy.easypay.design.components.ProductMockupCardDark
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.components.TransactionItem
import com.sethy.easypay.ui.screens.home.components.FeaturedGamesCarousel
import com.sethy.easypay.ui.screens.home.components.QuickActionsRow
import com.sethy.easypay.ui.state.HomeEffect
import com.sethy.easypay.ui.state.HomeEvent
import com.sethy.easypay.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSendMoney: () -> Unit,
    onNavigateToTransactionDetail: (String) -> Unit,
    onNavigateToStore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToStore -> onNavigateToStore()
                HomeEffect.NavigateToSendMoney -> onNavigateToSendMoney()
                is HomeEffect.NavigateToTransactionDetail ->
                    onNavigateToTransactionDetail(effect.id)
            }
        }
    }

    androidx.compose.material3.Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = null,
                showBackButton = false,
                actions = {
                    IconButton(onClick = { /* TODO: notifications */ }) {
                        Icon(
                            imageVector = Lucide.Bell,
                            contentDescription = "Notifications",
                            tint = Ink
                        )
                    }
                    IconButton(onClick = { /* TODO: settings */ }) {
                        Icon(
                            imageVector = Lucide.Settings,
                            contentDescription = "Settings",
                            tint = Ink
                        )
                    }
                }
            )
        },
        containerColor = Canvas
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md),
            contentPadding = PaddingValues(vertical = EasyPaySpacing.md)
        ) {
            item {
                BalanceHero(balance = state.balance)
            }
            item {
                QuickActionsRow(
                    onStoreClick = { viewModel.onEvent(HomeEvent.StoreClick) },
                    onSendClick = { viewModel.onEvent(HomeEvent.SendMoneyClick) },
                    onTopUpClick = { /* TODO: top up — Phase 4 */ }
                )
            }
            if (state.featuredGames.isNotEmpty()) {
                item {
                    SectionHeader(text = "Featured in the store")
                }
                item {
                    FeaturedGamesCarousel(
                        games = state.featuredGames,
                        onGameClick = { game ->
                            viewModel.onEvent(HomeEvent.StoreClick)
                        }
                    )
                }
            }
            item {
                SectionHeader(text = "Recent activity")
            }
            items(
                items = state.transactions,
                key = { it.id }
            ) { transaction ->
                Row(modifier = Modifier.padding(horizontal = EasyPaySpacing.md)) {
                    TransactionItem(
                        transaction = transaction,
                        onClick = {
                            viewModel.onEvent(HomeEvent.TransactionClick(transaction.id))
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }

            state.errorMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        style = EasyPayTypography.caption,
                        color = Error,
                        modifier = Modifier.padding(horizontal = EasyPaySpacing.md)
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceHero(balance: Double) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.padding(horizontal = EasyPaySpacing.md)
    ) {
        ProductMockupCardDark(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Total balance",
                    style = EasyPayTypography.caption,
                    color = OnDarkSoft
                )
                Spacer(modifier = Modifier.height(EasyPaySpacing.xs))
                CountUpText(
                    targetValue = balance,
                    textStyle = EasyPayTypography.displayLG,
                    color = OnDark,
                    format = { String.format("$%,.2f", it) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = EasyPayTypography.captionUppercase,
        color = Muted,
        modifier = Modifier.padding(horizontal = EasyPaySpacing.md)
    )
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
