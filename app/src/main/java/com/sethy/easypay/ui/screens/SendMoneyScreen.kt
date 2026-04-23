package com.sethy.easypay.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.sethy.easypay.R
import com.sethy.easypay.ui.components.PrimaryButton
import com.sethy.easypay.ui.components.SendAmountKeypad
import com.sethy.easypay.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SendMoneyScreen(
    recipientName: String,
    onSendClick: (Double) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf("0") }

    val entranceProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(150)
        entranceProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
    }

    val avatarScale by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.1f) 1f else 0.3f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "avatarScale"
    )
    val avatarAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.1f) 1f else 0f,
        animationSpec = tween(500),
        label = "avatarAlpha"
    )

    val infoOffset by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.25f) 0f else 40f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "infoOffset"
    )
    val infoAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.25f) 1f else 0f,
        animationSpec = tween(500),
        label = "infoAlpha"
    )

    val keypadOffset by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.4f) 0f else 80f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "keypadOffset"
    )
    val keypadAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.4f) 1f else 0f,
        animationSpec = tween(500),
        label = "keypadAlpha"
    )

    val sendButtonScale by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.55f) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "sendButtonScale"
    )
    val sendButtonAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.55f) 1f else 0f,
        animationSpec = tween(400),
        label = "sendButtonAlpha"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
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
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Lucide.ChevronLeft,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Send Money",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .scale(avatarScale)
                        .alpha(avatarAlpha)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Recipient",
                        modifier = Modifier
                            .size(116.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .offset(y = infoOffset.dp)
                        .alpha(infoAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = recipientName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "+91 8050530XXX",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedAmount(
                        amount = amount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .offset(y = keypadOffset.dp)
                            .alpha(keypadAlpha)
                    ) {
                        SendAmountKeypad(
                            onKeyClick = { key ->
                                amount = amount.applyKey(key)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .scale(sendButtonScale)
                            .alpha(sendButtonAlpha)
                    ) {
                        PrimaryButton(
                            text = "Send Money",
                            onClick = {
                                val amountValue = if (amount == "0") 0.0 else amount.toDouble() / 100
                                onSendClick(amountValue)
                            },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun AnimatedAmount(
    amount: String,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = amount,
        transitionSpec = {
            val direction = if (targetState.length > initialState.length || (targetState.toDoubleOrNull()
                    ?: 0.0) > (initialState.toDoubleOrNull() ?: 0.0)
            ) {
                slideInVertically { -it } + fadeIn(tween(150)) togetherWith
                slideOutVertically { it } + fadeOut(tween(150))
            } else {
                slideInVertically { it } + fadeIn(tween(150)) togetherWith
                slideOutVertically { -it } + fadeOut(tween(150))
            }
            direction
        },
        label = "amountAnimation",
        modifier = modifier
    ) { targetAmount ->
        Text(
            text = "$${targetAmount}",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

fun String.applyKey(key: String): String = when (key) {
    "⌫" -> if (length > 1) dropLast(1) else "0"

    "." -> {
        if (contains(".")) this
        else "$this."
    }

    else -> {
        if (!key.all { it.isDigit() }) return this

        val parts = split(".")
        val integerPart = parts[0]
        val decimalPart = parts.getOrNull(1)

        when {
            this == "0" -> key

            decimalPart != null -> {
                if (decimalPart.length >= 2) this
                else this + key
            }

            integerPart.length >= 8 -> this

            else -> this + key
        }
    }
}
