package com.sethy.easypay.navigation

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
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.sethy.easypay.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyPayNavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()

    var user by remember { mutableStateOf<User?>(null) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isCheckingAuth by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf(Route.Onboarding.route) }

    val authViewModel = remember {
        val tokenManager = AuthTokenManager(context)
        val authRepository = AuthRepository(tokenManager)
        AuthViewModel(authRepository)
    }

    LaunchedEffect(user) {
        if (user != null && transactions.isEmpty()) {
            withContext(Dispatchers.IO) {
                transactions = MockDataLoader.loadTransactions(context)
            }
        }
    }

    if (isCheckingAuth) {
        LaunchedEffect(Unit) {
            authViewModel.checkAuthState(
                onAuthenticated = { authenticatedUser ->
                    user = authenticatedUser
                    startDestination = Route.Home.route
                    isCheckingAuth = false
                },
                onUnauthenticated = {
                    startDestination = Route.Onboarding.route
                    isCheckingAuth = false
                }
            )
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Route.Onboarding.route) {
            OnboardingScreen(
                onLoginClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Onboarding.route)
                    }
                },
                onSignUpClick = {
                    navController.navigate(Route.Signup.route) {
                        popUpTo(Route.Onboarding.route)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Route.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { authenticatedUser ->
                    user = authenticatedUser
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Onboarding.route) { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate(Route.Signup.route)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Route.Signup.route) {
            SignupScreen(
                viewModel = authViewModel,
                onSignupSuccess = { authenticatedUser ->
                    user = authenticatedUser
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Onboarding.route)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Route.Home.route) {
            HomeScreen(
                user = user!!,
                transactions = transactions,
                onSendMoneyClick = {
                    navController.navigate(Route.SendMoney.create())
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = Route.SendMoney.route,
            arguments = listOf(
                navArgument("recipientName") {
                    type = NavType.StringType
                    defaultValue = "Nayantara V"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val recipientName = backStackEntry.arguments?.getString("recipientName") ?: "Nayantara V"

            SendMoneyScreen(
                recipientName = recipientName,
                onSendClick = { amount ->
                    user = user!!.copy(balance = user!!.balance - amount)
                    transactions = listOf(
                        Transaction(
                            id = System.currentTimeMillis().toString(),
                            recipientName = recipientName,
                            amount = amount,
                            type = TransactionType.SENT
                        )
                    ) + transactions
                    navController.navigate(Route.TransferSuccess.create(recipientName, amount))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = Route.TransferSuccess.route,
            arguments = listOf(
                navArgument("recipientName") { type = NavType.StringType },
                navArgument("amount") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val recipientName = backStackEntry.arguments?.getString("recipientName") ?: ""
            val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0

            TransferSuccessScreen(
                recipientName = recipientName,
                amount = amount,
                onDone = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Home.route) { inclusive = true }
                    }
                },
                onTransferMore = {
                    navController.navigate(Route.SendMoney.create()) {
                        popUpTo(Route.Home.route)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
