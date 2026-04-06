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
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import com.sethy.easypay.ui.screens.HomeScreen
import com.sethy.easypay.ui.screens.OnboardingScreen
import com.sethy.easypay.ui.screens.SendMoneyScreen
import com.sethy.easypay.ui.screens.TransferSuccessScreen
import com.sethy.easypay.ui.screens.auth.LoginScreen
import com.sethy.easypay.ui.screens.auth.SignupScreen
import com.sethy.easypay.ui.theme.EasyPayTheme
import com.sethy.easypay.ui.viewmodel.AuthViewModel
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
    object Login : Screen()
    object Signup : Screen()
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
    var isCheckingAuth by remember { mutableStateOf(true) }
    val authViewModel = remember {
        val tokenManager = AuthTokenManager(context)
        val authRepository = AuthRepository(tokenManager)
        AuthViewModel(authRepository)
    }
    LaunchedEffect(Unit) {
        authViewModel.checkAuthState(
            onAuthenticated = { authenticatedUser ->
                user = authenticatedUser
                currentScreen = Screen.Home
                isCheckingAuth = false
            },
            onUnauthenticated = {
                isCheckingAuth = false
            }
        )
    }
    LaunchedEffect(user) {
        if (user != null && transactions.isEmpty()) {
            withContext(Dispatchers.IO) {
                transactions = MockDataLoader.loadTransactions(context)
            }
        }
    }
    if (isCheckingAuth) {
        return
    }

    when (val screen = currentScreen) {
        is Screen.Onboarding -> {
            OnboardingScreen(
                onLoginClick = { currentScreen = Screen.Login },
                onSignUpClick = { currentScreen = Screen.Signup },
                modifier = Modifier.fillMaxSize()
            )
        }

        is Screen.Login -> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { authenticatedUser ->
                    user = authenticatedUser
                    currentScreen = Screen.Home
                },
                onSignupClick = { currentScreen = Screen.Signup },
                onBackClick = { currentScreen = Screen.Onboarding },
                modifier = Modifier.fillMaxSize()
            )
        }

        is Screen.Signup -> {
            SignupScreen(
                viewModel = authViewModel,
                onSignupSuccess = { authenticatedUser ->
                    user = authenticatedUser
                    currentScreen = Screen.Home
                },
                onLoginClick = { currentScreen = Screen.Login },
                onBackClick = { currentScreen = Screen.Onboarding },
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
                onDone = { currentScreen = Screen.Home },
                onTransferMore = { currentScreen = Screen.SendMoney() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
