package com.sethy.easypay.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.BadgeDollarSign
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.CirclePlus
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Wallet
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User
import com.sethy.easypay.ui.components.AppTopBar
import com.sethy.easypay.ui.components.BottomNavItem
import com.sethy.easypay.ui.components.AppBottomBar
import com.sethy.easypay.ui.components.QuickActionItem
import com.sethy.easypay.ui.components.TransactionItem
import com.sethy.easypay.ui.theme.OffWhite
import java.util.Locale

@Composable
fun HomeScreen(
    user: User,
    transactions: List<Transaction>,
    onSendMoneyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            AppBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onScanClicked = { /* Handle scan click */ }
            )
        },
        containerColor = OffWhite
    ){
        paddingValues -> Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
                start = 16.dp,
                end = 16.dp,
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column() {
                Text("Hi, ${user.name}", style = MaterialTheme.typography.titleMedium)

                Text("Your available balance", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Text(
                "$${String.format(Locale.getDefault(), "%.2f", user.balance)}",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = modifier
            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(6.dp))
            .padding(12.dp)){
            Button(onClick = {}, shape = RoundedCornerShape(4.dp)){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                   Icon(imageVector = Lucide.CirclePlus, contentDescription = "Add")

                    Text("Top Up")
                }
            }

            Button(onClick = {}, shape = RoundedCornerShape(4.dp)){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                   Icon(imageVector = Lucide.Send, contentDescription = "Send")

                    Text("Send")
                }
            }

            Button(onClick = {}, shape = RoundedCornerShape(4.dp)){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                   Icon(imageVector = Lucide.BadgeDollarSign, contentDescription = null)

                    Text("Withdraw")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = Lucide.Send,
                    label = "Send",
                    onClick = onSendMoneyClick
                )
                QuickActionItem(
                    icon = Lucide.QrCode,
                    label = "QR Pay",
                    onClick = { }
                )
                QuickActionItem(
                    icon = Lucide.MapPin,
                    label = "Nearby",
                    onClick = { }
                )
                QuickActionItem(
                    icon = Lucide.Phone,
                    label = "Mobile",
                    onClick = { }
                )
                QuickActionItem(
                    icon = Lucide.Wallet,
                    label = "Wallet",
                    onClick = { }
                )
                QuickActionItem(
                    icon = Lucide.Receipt,
                    label = "Bills",
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
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
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "On your first transfer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.ShoppingCart,
                            contentDescription = "ShoppingCart",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            transactions.forEach { transaction ->
                TransactionItem(transaction = transaction)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
    }
}
