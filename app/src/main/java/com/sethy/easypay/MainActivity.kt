package com.sethy.easypay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sethy.easypay.data.MockDataLoader
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.model.User
import com.sethy.easypay.ui.screens.HomeScreen
import com.sethy.easypay.ui.screens.OnboardingScreen
import com.sethy.easypay.ui.screens.SendMoneyScreen
import com.sethy.easypay.ui.screens.TransferSuccessScreen
import com.sethy.easypay.ui.theme.EasyPayTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyPayTheme {
                EasyPayApp()
            }
        }
    }
}

sealed class Screen {
    object Onboarding : Screen()
    object Home : Screen()
    data class SendMoney(val recipientName: String = "Nayantara V") : Screen()
    data class TransferSuccess(val recipientName: String, val amount: Double) : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyPayApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Onboarding) }
    var user by remember { mutableStateOf<User?>(null) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    // Load mock data from assets
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            user = MockDataLoader.loadUser(context)
            transactions = MockDataLoader.loadTransactions(context)
        }
    }

    // Show loading state while data loads
    if (user == null) {
        // Could add a loading screen here
        return
    }

    when (val screen = currentScreen) {
        is Screen.Onboarding -> {
            OnboardingScreen(
                onLoginClick = { currentScreen = Screen.Home },
                onSignUpClick = { currentScreen = Screen.Home },
                modifier = Modifier.fillMaxSize()
            )
        }

        is Screen.Home -> {
            HomeScreen(
                user = user!!,
                transactions = transactions,
                onSendMoneyClick = { currentScreen = Screen.SendMoney() },
                modifier = Modifier.fillMaxSize()
            )
        }

        is Screen.SendMoney -> {
            SendMoneyScreen(
                recipientName = screen.recipientName,
                onSendClick = { amount ->
                    user = user!!.copy(balance = user!!.balance - amount)
                    transactions = listOf(
                        Transaction(
                            id = System.currentTimeMillis().toString(),
                            recipientName = screen.recipientName,
                            amount = amount,
                            type = TransactionType.SENT
                        )
                    ) + transactions
                    currentScreen = Screen.TransferSuccess(screen.recipientName, amount)
                },
                onBackClick = { currentScreen = Screen.Home },
                modifier = Modifier.fillMaxSize()
            )
        }

        is Screen.TransferSuccess -> {
            TransferSuccessScreen(
                recipientName = screen.recipientName,
                amount = screen.amount,
                onDoneClick = { currentScreen = Screen.Home },
                onTransferMoreClick = { currentScreen = Screen.SendMoney() },
                onBackClick = { currentScreen = Screen.Home },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
