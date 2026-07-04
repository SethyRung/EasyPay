package com.sethy.easypay.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AuthGate(
    viewModel: AppSessionViewModel = hiltViewModel()
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val onboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val showLoading = isAuthenticated == null || onboardingCompleted == null
    val showOnboarding = !showLoading && onboardingCompleted == false

    when {
        showLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        showOnboarding -> EasyPayNavGraph(startDestination = Route.Onboarding.route)
        isAuthenticated == false -> EasyPayNavGraph(startDestination = Route.Login.route)
        else -> EasyPayNavGraph(startDestination = Route.Home.route)
    }
}
