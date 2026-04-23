package com.sethy.easypay.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.BadgeCheck
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.sethy.easypay.ui.components.PrimaryButton
import com.sethy.easypay.ui.components.SecondaryButton
import com.sethy.easypay.ui.theme.Success
import com.sethy.easypay.ui.theme.SuccessSoft
import com.sethy.easypay.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun TransferSuccessScreen(
    recipientName: String,
    amount: Double,
    onDone: () -> Unit,
    onTransferMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(100)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1400, easing = FastOutSlowInEasing)
        )
    }

    val iconScale by animateFloatAsState(
        targetValue = if (progress.value > 0.08f) 1f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "iconScale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (progress.value > 0.08f) 1f else 0f,
        animationSpec = tween(400),
        label = "iconAlpha"
    )

    val contentOffset by animateFloatAsState(
        targetValue = if (progress.value > 0.35f) 0f else 40f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "contentOffset"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (progress.value > 0.35f) 1f else 0f,
        animationSpec = tween(500),
        label = "contentAlpha"
    )

    val receiptOffset by animateFloatAsState(
        targetValue = if (progress.value > 0.55f) 0f else 50f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "receiptOffset"
    )
    val receiptAlpha by animateFloatAsState(
        targetValue = if (progress.value > 0.55f) 1f else 0f,
        animationSpec = tween(500),
        label = "receiptAlpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (progress.value > 0.75f) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "buttonScale"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (progress.value > 0.75f) 1f else 0f,
        animationSpec = tween(400),
        label = "buttonAlpha"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDone) {
                    Icon(
                        imageVector = Lucide.ChevronLeft,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Transfer Receipt",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .scale(iconScale)
                        .alpha(iconAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    PulsingSuccessIcon(
                        enabled = progress.value > 0.9f
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    modifier = Modifier
                        .offset(y = contentOffset.dp)
                        .alpha(contentAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Transfer Successful",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = String.format("$%.2f", amount),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "to $recipientName",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = receiptOffset.dp)
                        .alpha(receiptAlpha)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ReceiptRow(label = "Recipient", value = recipientName)
                            ReceiptRow(label = "Amount", value = String.format("$%.2f", amount))
                            ReceiptRow(label = "Fee", value = "$0.00")
                            ReceiptRow(label = "Status", value = "Completed", isHighlight = true)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .scale(buttonScale)
                    .alpha(buttonAlpha)
            ) {
                PrimaryButton(
                    text = "Done",
                    onClick = onDone
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecondaryButton(
                    text = "Transfer More",
                    onClick = onTransferMore
                )
            }
        }
    }
}

@Composable
private fun PulsingSuccessIcon(
    enabled: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val scale = if (enabled) pulse else 1f

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(SuccessSoft)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Lucide.BadgeCheck,
            contentDescription = "Success",
            tint = Success,
            modifier = Modifier.size(56.dp)
        )
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isHighlight) Success else MaterialTheme.colorScheme.onBackground
        )
    }
}
