@file:OptIn(ExperimentalMaterial3Api::class)

package com.sethy.easypay.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.components.BottomNav
import com.sethy.easypay.design.components.BottomNavItem
import com.sethy.easypay.ui.screens.auth.LoginScreen
import com.sethy.easypay.ui.screens.auth.SignupScreen
import com.sethy.easypay.ui.screens.bridge.BridgeEventLogScreen
import com.sethy.easypay.ui.screens.bridge.BridgeStoreScreen
import com.sethy.easypay.ui.screens.calendar.CalendarScreen
import com.sethy.easypay.ui.screens.home.HomeScreen
import com.sethy.easypay.ui.screens.notifications.NotificationsScreen
import com.sethy.easypay.ui.screens.onboarding.OnboardingScreen
import com.sethy.easypay.ui.screens.profile.ProfileScreen
import com.sethy.easypay.ui.screens.send.SendMoneyScreen
import com.sethy.easypay.ui.screens.send.TransferSuccessScreen
import com.sethy.easypay.ui.screens.settings.SettingsScreen
import com.sethy.easypay.ui.screens.transactions.TransactionDetailScreen
import com.sethy.easypay.ui.screens.wallet.TopUpScreen

private const val SLIDE_DURATION = 250
private const val SCALE_DURATION = 400

private val MainTabRoutes = setOf(
    Route.Home.route,
    Route.Notifications.route,
    Route.Profile.route
)

private fun routeToTab(route: String?): BottomNavItem? = when (route) {
    Route.Home.route -> BottomNavItem.Home
    Route.Notifications.route -> BottomNavItem.Notifications
    Route.Profile.route -> BottomNavItem.Profile
    else -> null
}

private fun tabToRoute(tab: BottomNavItem): String = when (tab) {
    BottomNavItem.Home -> Route.Home.route
    BottomNavItem.Store -> Route.Store.route
    BottomNavItem.Notifications -> Route.Notifications.route
    BottomNavItem.Profile -> Route.Profile.route
}

@Composable
fun EasyPayNavGraph(
    startDestination: String = Route.Login.route
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedTab = routeToTab(currentRoute)
    val showBottomBar = selectedTab != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar && selectedTab != null) {
                BottomNav(
                    selectedItem = selectedTab,
                    onItemSelected = { tab -> navigateToTab(navController, tab) }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars.only(
            WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
        ),
        containerColor = Canvas
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            enterTransition = { defaultEnter() },
            exitTransition = { defaultExit() },
            popEnterTransition = { defaultPopEnter() },
            popExitTransition = { defaultPopExit() }
        ) {
            composable(
                route = Route.Onboarding.route,
                enterTransition = { fadeIn(tween(SLIDE_DURATION)) },
                exitTransition = { fadeOut(tween(SLIDE_DURATION)) },
                popEnterTransition = { fadeIn(tween(SLIDE_DURATION)) },
                popExitTransition = { fadeOut(tween(SLIDE_DURATION)) }
            ) {
                OnboardingScreen(
                    onNavigateToLogin = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Onboarding.route) { inclusive = true }
                        }
                    },
                    onSignInClick = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Route.Login.route,
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    },
                    onSignupClick = { navController.navigate(Route.Signup.route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Route.Signup.route,
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                SignupScreen(
                    onSignupSuccess = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Route.Home.route,
                enterTransition = { scaleFadeEnter() },
                exitTransition = { fadeOut(tween(SLIDE_DURATION)) },
                popEnterTransition = { scaleFadeEnter() },
                popExitTransition = { fadeOut(tween(SLIDE_DURATION)) }
            ) {
                HomeScreen(
                    onNavigateToSendMoney = { navController.navigate(Route.SendMoney.create()) },
                    onNavigateToTransactionDetail = { id ->
                        navController.navigate(Route.TransactionDetail.create(id))
                    },
                    onNavigateToStore = {
                        navController.navigate(Route.Store.route)
                    },
                    onNavigateToTopUp = {
                        navController.navigate(Route.TopUp.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Route.Settings.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Route.Notifications.route)
                    }
                )
            }

            composable(
                route = Route.Store.route,
                enterTransition = { slideUpEnter() },
                exitTransition = { slideDownExit() },
                popEnterTransition = { slideUpEnter() },
                popExitTransition = { slideDownExit() }
            ) {
                BridgeStoreScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToTopUp = {
                        navController.navigate(Route.TopUp.route)
                    }
                )
            }

            composable(
                route = Route.TopUp.route,
                enterTransition = { slideUpEnter() },
                exitTransition = { slideDownExit() },
                popEnterTransition = { slideUpEnter() },
                popExitTransition = { slideDownExit() }
            ) {
                TopUpScreen(
                    onBackClick = { navController.popBackStack() },
                    onDone = { navController.popBackStack() }
                )
            }

            composable(
                route = Route.BridgeEventLog.route,
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                BridgeEventLogScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Route.Notifications.route,
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                NotificationsScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToTransactionDetail = { id ->
                        navController.navigate(Route.TransactionDetail.create(id))
                    }
                )
            }

            composable(
                route = Route.Profile.route,
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                ProfileScreen(
                    onNavigateToLogin = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToBridgeEventLog = {
                        navController.navigate(Route.BridgeEventLog.route)
                    },
                    onBackClick = { navController.popBackStack() }
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
                enterTransition = { slideUpEnter() },
                exitTransition = { slideDownExit() },
                popEnterTransition = { slideUpEnter() },
                popExitTransition = { slideDownExit() }
            ) { backStackEntry ->
                val recipientName = backStackEntry.arguments?.getString("recipientName") ?: "Nayantara V"
                SendMoneyScreen(
                    recipientName = recipientName,
                    onNavigateToTransferSuccess = { recipient, amount ->
                        navController.navigate(Route.TransferSuccess.create(recipient, amount))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Route.TransferSuccess.route,
                arguments = listOf(
                    navArgument("recipientName") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.FloatType }
                ),
                enterTransition = { scaleFadeEnter() },
                exitTransition = { fadeOut(tween(SLIDE_DURATION)) },
                popEnterTransition = { scaleFadeEnter() },
                popExitTransition = { fadeOut(tween(SLIDE_DURATION)) }
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
                    }
                )
            }

            composable(
                route = Route.TransactionDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                TransactionDetailScreen(onBackClick = { navController.popBackStack() })
            }

            composable(
                route = Route.Settings.route,
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToEditProfile = { navController.navigate(Route.Profile.route) },
                    onNavigateToCalendar = { navController.navigate(Route.Calendar.route) }
                )
            }

            composable(
                route = Route.Calendar.route,
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                CalendarScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}

private fun navigateToTab(navController: NavHostController, tab: BottomNavItem) {
    navController.navigate(tabToRoute(tab)) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
            inclusive = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

// ── Transitions ───────────────────────────────────────────────────────────────

private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultEnter() =
    fadeIn(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultExit() =
    fadeOut(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopEnter() =
    fadeIn(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopExit() =
    fadeOut(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideLeftEnter() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) + fadeIn(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideLeftExit() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) + fadeOut(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideRightEnter() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) + fadeIn(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideRightExit() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) + fadeOut(tween(SLIDE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideUpEnter() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) + fadeIn(tween(350))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideDownExit() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) + fadeOut(tween(300))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.scaleFadeEnter() =
    scaleIn(
        initialScale = 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) + fadeIn(tween(SCALE_DURATION))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.scaleFadeExit() =
    scaleOut(
        targetScale = 1.05f,
        animationSpec = tween(300)
    ) + fadeOut(tween(300))