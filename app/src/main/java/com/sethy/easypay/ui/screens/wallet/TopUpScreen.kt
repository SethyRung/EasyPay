package com.sethy.easypay.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
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
import com.sethy.easypay.design.OnDarkSoft
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.SurfaceCard
import com.sethy.easypay.design.SurfaceDark
import com.sethy.easypay.design.components.CountUpText
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.viewmodel.TopUpEvent
import com.sethy.easypay.ui.viewmodel.TopUpViewModel

@Composable
fun TopUpScreen(
    viewModel: TopUpViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { onDone() }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Top up",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(EasyPayRadius.lg))
                    .background(SurfaceDark)
                    .padding(EasyPaySpacing.lg)
            ) {
                Column {
                    Text(
                        text = "Current balance",
                        style = EasyPayTypography.caption,
                        color = OnDarkSoft
                    )
                    Spacer(modifier = Modifier.height(EasyPaySpacing.xs))
                    CountUpText(
                        targetValue = state.balance,
                        textStyle = EasyPayTypography.displayMD,
                        color = OnDark,
                        format = { String.format("$%,.2f", it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            Text(
                text = "Add to wallet",
                style = EasyPayTypography.titleMD,
                color = Ink,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
            ) {
                viewModel.presetAmounts.chunked(2).forEach { rowAmounts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
                    ) {
                        rowAmounts.forEach { amount ->
                            AmountTile(
                                amount = amount,
                                selected = state.amountMajor == amount,
                                onClick = { viewModel.onEvent(TopUpEvent.AmountSelected(amount)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    style = EasyPayTypography.caption,
                    color = Error,
                    modifier = Modifier.padding(bottom = EasyPaySpacing.md)
                )
            }

            Button(
                onClick = { viewModel.onEvent(TopUpEvent.Confirm) },
                enabled = !state.isLoading && state.amountMajor != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(EasyPayRadius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnDark,
                    disabledContainerColor = Primary.copy(alpha = 0.4f),
                    disabledContentColor = OnDark
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(20.dp),
                        color = OnDark,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(imageVector = Lucide.Plus, contentDescription = null, tint = OnDark)
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(
                        text = "Add " + state.amountMajor?.let { "$%.2f".format(it) }.orEmpty(),
                        style = EasyPayTypography.button
                    )
                }
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))
        }
    }
}

@Composable
private fun AmountTile(
    amount: Double,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) Primary else SurfaceCard
    val fg = if (selected) OnDark else Ink
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(EasyPayRadius.lg))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$%.0f".format(amount),
            style = EasyPayTypography.titleLG.copy(fontWeight = FontWeight.SemiBold),
            color = fg,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun TopUpScreenPreview() {
    EasyPayTheme {
        TopUpScreen(onBackClick = {}, onDone = {})
    }
}