package com.sethy.easypay.ui.screens.bridge

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Loader
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.Wallet
import com.sethy.easypay.bridge.BridgePaymentRequest
import com.sethy.easypay.bridge.PaymentSheetState
import com.sethy.easypay.design.AccentAmber
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.Hairline
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDark
import com.sethy.easypay.design.OnDarkSoft
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.SurfaceDark
import com.sethy.easypay.design.SurfaceDarkElevated
import com.sethy.easypay.design.Success

@Composable
fun PaymentSheetContent(
    state: PaymentSheetState,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onTopUp: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleState = state
    if (visibleState is PaymentSheetState.Hidden) return

    val request = when (visibleState) {
        is PaymentSheetState.Confirming -> visibleState.request
        is PaymentSheetState.Processing -> visibleState.request
        is PaymentSheetState.Success -> visibleState.request
        is PaymentSheetState.InsufficientFunds -> visibleState.request
        is PaymentSheetState.Error -> visibleState.request
        PaymentSheetState.Hidden -> return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = EasyPaySpacing.xl)
            .padding(top = EasyPaySpacing.md, bottom = EasyPaySpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Hairline)
        )
        Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

        Text(
            text = "Confirm purchase",
            style = EasyPayTypography.titleLG,
            color = Ink
        )
        Spacer(modifier = Modifier.height(EasyPaySpacing.md))

        GameCard(
            name = firstItemName(request) ?: request.note,
            subtitle = request.billerCode
        )

        Spacer(modifier = Modifier.height(EasyPaySpacing.md))

        InfoRow("Merchant", request.billerCode.replaceFirstChar { it.uppercase() })
        InfoRow("Order ref", "#${request.merchantRef}")
        if (request.note.isNotBlank()) {
            InfoRow("Note", request.note)
        }

        Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Total",
                style = EasyPayTypography.caption,
                color = Muted
            )
            Text(
                text = formatMoney(request.amountMajor, request.currency),
                style = EasyPayTypography.displaySM,
                color = Ink
            )
        }

        Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

        when (visibleState) {
            is PaymentSheetState.Confirming -> {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(EasyPayRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = OnDark
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text("Confirm payment", style = EasyPayTypography.button)
                }
                Spacer(modifier = Modifier.height(EasyPaySpacing.md))
                TextButton(onClick = onCancel) {
                    Text("Cancel", style = EasyPayTypography.button, color = Muted)
                }
            }

            is PaymentSheetState.Processing -> {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(EasyPayRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary.copy(alpha = 0.5f),
                        contentColor = OnDark
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = OnDark,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Processing", style = EasyPayTypography.button)
                }
            }

            is PaymentSheetState.Success -> {
                StatusCallout(
                    icon = Lucide.Check,
                    title = "Payment confirmed",
                    body = "Thanks! Sending you back to the store.",
                    tint = Success
                )
                Spacer(modifier = Modifier.height(EasyPaySpacing.md))
                TextButton(onClick = onDismiss) {
                    Text("Done", style = EasyPayTypography.button, color = Primary)
                }
            }

            is PaymentSheetState.InsufficientFunds -> {
                StatusCallout(
                    icon = Lucide.Wallet,
                    title = "Insufficient funds",
                    body = "Top up your wallet to continue.",
                    tint = AccentAmber
                )
                Spacer(modifier = Modifier.height(EasyPaySpacing.md))
                Button(
                    onClick = onTopUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(EasyPayRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = OnDark
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text("Top up to continue", style = EasyPayTypography.button)
                }
                Spacer(modifier = Modifier.height(EasyPaySpacing.xs))
                TextButton(onClick = onCancel) {
                    Text("Cancel", style = EasyPayTypography.button, color = Muted)
                }
            }

            is PaymentSheetState.Error -> {
                StatusCallout(
                    icon = Lucide.TriangleAlert,
                    title = "Payment failed",
                    body = visibleState.message,
                    tint = Error
                )
                Spacer(modifier = Modifier.height(EasyPaySpacing.md))
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(EasyPayRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = OnDark
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text("Try again", style = EasyPayTypography.button)
                }
                Spacer(modifier = Modifier.height(EasyPaySpacing.xs))
                TextButton(onClick = onCancel) {
                    Text("Cancel", style = EasyPayTypography.button, color = Muted)
                }
            }
        }
    }
}

@Composable
private fun GameCard(name: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EasyPayRadius.lg))
            .background(SurfaceDarkElevated)
            .padding(EasyPaySpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(EasyPayRadius.md))
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.Wallet,
                contentDescription = null,
                tint = OnDark,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = EasyPayTypography.titleSM.copy(fontWeight = FontWeight.SemiBold),
                color = OnDark
            )
            Text(
                text = subtitle,
                style = EasyPayTypography.bodySM,
                color = OnDarkSoft
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = EasyPayTypography.bodySM,
            color = Muted
        )
        Text(
            text = value,
            style = EasyPayTypography.bodyMD.copy(fontWeight = FontWeight.Medium),
            color = Ink
        )
    }
}

@Composable
private fun StatusCallout(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    tint: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(EasyPayRadius.md),
        color = tint.copy(alpha = 0.12f),
        contentColor = tint
    ) {
        Row(
            modifier = Modifier.padding(EasyPaySpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = EasyPayTypography.titleSM.copy(fontWeight = FontWeight.SemiBold),
                    color = tint
                )
                Text(
                    text = body,
                    style = EasyPayTypography.bodySM,
                    color = tint
                )
            }
        }
    }
}

private fun firstItemName(request: BridgePaymentRequest): String? =
    request.items.firstOrNull()?.name

private fun formatMoney(amount: Double, currency: String): String =
    if (currency.equals("USD", ignoreCase = true)) "$%.2f".format(amount) else "%.2f %s".format(amount, currency)