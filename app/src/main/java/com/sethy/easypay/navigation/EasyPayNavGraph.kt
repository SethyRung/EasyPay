package com.sethy.easypay.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sethy.easypay.data.MockDataLoader
import com.sethy.easypay.data.api.ApiProvider
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import com.sethy.easypay.ui.screens.HomeScreen
import com.sethy.easypay.ui.screens.OnboardingScreen
import com.sethy.easypay.ui.screens.ProfileScreen
import com.sethy.easypay.ui.screens.SendMoneyScreen
import com.sethy.easypay.ui.screens.TransferSuccessScreen
import com.sethy.easypay.ui.screens.auth.LoginScreen
import com.sethy.easypay.ui.screens.auth.SignupScreen
import com.sethy.easypay.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TRANSITION_DURATION = 350

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
        val apiProvider = ApiProvider(tokenManager)
        val authRepository = AuthRepository(apiProvider, tokenManager)
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
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(TRANSITION_DURATION))
        }
    ) {
        composable(
            route = Route.Onboarding.route,
            enterTransition = {
                scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(500)
                ) + fadeIn(tween(400))
            },
            exitTransition = {
                scaleOut(
                    targetScale = 1.05f,
                    animationSpec = tween(300)
                ) + fadeOut(tween(300))
            }
        ) {
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

        composable(
            route = Route.Login.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(250))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(250))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(250))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(250))
            }
        ) {
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

        composable(
            route = Route.Signup.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(250))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(250))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(250))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(250))
            }
        ) {
            SignupScreen(
                viewModel = authViewModel,
                onSignupSuccess = { authenticatedUser ->
                    user = authenticatedUser
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Onboarding.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = Route.Home.route,
            enterTransition = {
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(tween(400))
            },
            exitTransition = {
                fadeOut(tween(300))
            }
        ) {
            HomeScreen(
                user = user!!,
                transactions = transactions,
                onSendMoneyClick = {
                    navController.navigate(Route.SendMoney.create())
                },
                onProfileClick = {
                    navController.navigate(Route.Profile.route)
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
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(300))
            }
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
            ),
            enterTransition = {
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(tween(400))
            },
            exitTransition = {
                fadeOut(tween(300))
            }
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

        composable(
            route = Route.Profile.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(tween(300))
            }
        ) {
            // Guard against null user during logout transition
            val u = user
            if (u != null) {
                ProfileScreen(
                    user = u,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogoutClick = {
                        authViewModel.logout {
                            // Navigate first — avoids recomposition crash
                            // while Profile is still visible during transition
                            navController.navigate(Route.Onboarding.route) {
                                popUpTo(Route.Home.route) { inclusive = true }
                            }
                            user = null
                            transactions = emptyList()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
