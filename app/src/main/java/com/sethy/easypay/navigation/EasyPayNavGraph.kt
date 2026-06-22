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
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sethy.easypay.ui.screens.auth.LoginScreen
import com.sethy.easypay.ui.screens.auth.SignupScreen
import com.sethy.easypay.ui.screens.home.HomeScreen
import com.sethy.easypay.ui.screens.calendar.CalendarScreen
import com.sethy.easypay.ui.screens.notifications.NotificationsScreen
import com.sethy.easypay.ui.screens.profile.ProfileScreen
import com.sethy.easypay.ui.screens.send.SendMoneyScreen
import com.sethy.easypay.ui.screens.send.TransferSuccessScreen
import com.sethy.easypay.ui.screens.transactions.TransactionDetailScreen

private const val SLIDE_DURATION = 250
private const val SCALE_DURATION = 400

@Composable
fun EasyPayNavGraph(
    startDestination: String = Route.Login.route
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { defaultEnter() },
        exitTransition = { defaultExit() },
        popEnterTransition = { defaultPopEnter() },
        popExitTransition = { defaultPopExit() }
    ) {
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
                onNavigateToNotifications = { navController.navigate(Route.Notifications.route) },
                onNavigateToProfile = { navController.navigate(Route.Profile.route) },
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
            CalendarScreen(
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
            NotificationsScreen(onBackClick = { navController.popBackStack() })
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

