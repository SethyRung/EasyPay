package com.sethy.easypay.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User
import com.sethy.easypay.ui.components.AppTopBar
import com.sethy.easypay.ui.components.BottomNavItem
import com.sethy.easypay.ui.components.AppBottomBar
import com.sethy.easypay.ui.components.QuickActionItem
import com.sethy.easypay.ui.components.TransactionItem
import com.sethy.easypay.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun HomeScreen(
    user: User,
    transactions: List<Transaction>,
    onSendMoneyClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }

    LaunchedEffect(selectedTab) {
        if (selectedTab == BottomNavItem.PROFILE) {
            onProfileClick()
            selectedTab = BottomNavItem.HOME
        }
    }

    val entranceProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        entranceProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    val heroOffset by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.05f) 0f else 100f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "heroOffset"
    )
    val heroAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.05f) 1f else 0f,
        animationSpec = tween(600),
        label = "heroAlpha"
    )

    val quickActionsOffset by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.25f) 0f else 80f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "quickActionsOffset"
    )
    val quickActionsAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.25f) 1f else 0f,
        animationSpec = tween(500),
        label = "quickActionsAlpha"
    )

    val promoOffset by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.45f) 0f else 60f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "promoOffset"
    )
    val promoAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.45f) 1f else 0f,
        animationSpec = tween(500),
        label = "promoAlpha"
    )

    val transactionsAlpha by animateFloatAsState(
        targetValue = if (entranceProgress.value > 0.6f) 1f else 0f,
        animationSpec = tween(400),
        label = "transactionsAlpha"
    )

    val visibleTransactions = remember { mutableStateListOf<Transaction>() }
    var previousTransactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    LaunchedEffect(transactions) {
        val newItems = transactions.filter { it !in previousTransactions }
        visibleTransactions.clear()
        visibleTransactions.addAll(previousTransactions)
        previousTransactions = transactions

        newItems.forEachIndexed { index, transaction ->
            delay((index * 70).toLong().coerceAtMost(350))
            visibleTransactions.add(0, transaction)
        }
    }

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            AppBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onScanClicked = { /* Handle scan click */ }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 20.dp,
                    end = 20.dp,
                )
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = heroOffset.dp)
                    .alpha(heroAlpha)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = Color.Transparent,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF1A1A2E),
                                        Color(0xFF16213E)
                                    )
                                ),
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                text = "Hi, ${user.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Available Balance",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            AnimatedBalance(
                                balance = user.balance,
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ActionChip(
                                    icon = Lucide.CirclePlus,
                                    label = "Top Up",
                                    onClick = { },
                                    modifier = Modifier.weight(1f)
                                )
                                ActionChip(
                                    icon = Lucide.Send,
                                    label = "Send",
                                    onClick = onSendMoneyClick,
                                    modifier = Modifier.weight(1f)
                                )
                                ActionChip(
                                    icon = Lucide.BadgeDollarSign,
                                    label = "Withdraw",
                                    onClick = { },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .offset(y = quickActionsOffset.dp)
                    .alpha(quickActionsAlpha)
            ) {
                Column {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = SurfaceSubtle,
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                QuickActionItem(
                                    icon = Lucide.Globe,
                                    label = "Internet",
                                    onClick = { },
                                    iconTint = AccentBlue,
                                    iconBackground = AccentBlueSoft,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionItem(
                                    icon = Lucide.Droplet,
                                    label = "Water",
                                    onClick = { },
                                    iconTint = AccentCyan,
                                    iconBackground = AccentCyanSoft,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionItem(
                                    icon = Lucide.Zap,
                                    label = "Electricity",
                                    onClick = { },
                                    iconTint = AccentAmber,
                                    iconBackground = AccentAmberSoft,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionItem(
                                    icon = Lucide.Tv,
                                    label = "TV Cable",
                                    onClick = { },
                                    iconTint = AccentPurple,
                                    iconBackground = AccentPurpleSoft,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                QuickActionItem(
                                    icon = Lucide.Car,
                                    label = "Vehicle",
                                    onClick = { },
                                    iconTint = AccentGreen,
                                    iconBackground = AccentGreenSoft,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionItem(
                                    icon = Lucide.House,
                                    label = "Rent Bill",
                                    onClick = { },
                                    iconTint = AccentRose,
                                    iconBackground = AccentRoseSoft,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionItem(
                                    icon = Lucide.Landmark,
                                    label = "Invest",
                                    onClick = { },
                                    iconTint = AccentIndigo,
                                    iconBackground = AccentIndigoSoft,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionItem(
                                    icon = Lucide.LayoutGrid,
                                    label = "More",
                                    onClick = { },
                                    iconTint = AccentGray,
                                    iconBackground = AccentGraySoft,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .offset(y = promoOffset.dp)
                    .alpha(promoAlpha)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = AccentAmberSoft,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "30% OFF",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AccentAmber
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "On your first transfer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = AccentAmber,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.ShoppingCart,
                                contentDescription = "ShoppingCart",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(transactionsAlpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                visibleTransactions.forEachIndexed { index, transaction ->
                    val delayMultiplier = (index + 1).coerceAtMost(5)
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow,
                                visibilityThreshold = null
                            )
                        ) + fadeIn(
                            tween(300, delayMillis = delayMultiplier * 50)
                        )
                    ) {
                        TransactionItem(transaction = transaction)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AnimatedBalance(
    balance: Double,
    style: androidx.compose.ui.text.TextStyle,
    color: Color
) {
    var displayedBalance by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(balance) {
        val start = displayedBalance
        val duration = 800
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = (elapsed / duration.toFloat()).coerceIn(0f, 1f)
            val eased = FastOutSlowInEasing.transform(progress)
            displayedBalance = start + (balance - start) * eased
            if (progress >= 1f) break
            delay(16)
        }
        displayedBalance = balance
    }

    Text(
        text = "$${String.format(Locale.getDefault(), "%.2f", displayedBalance)}",
        style = style,
        color = color
    )
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "actionChipScale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(120)
            isPressed = false
        }
    }

    Surface(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier.scale(scale),
        shape = MaterialTheme.shapes.medium,
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}
