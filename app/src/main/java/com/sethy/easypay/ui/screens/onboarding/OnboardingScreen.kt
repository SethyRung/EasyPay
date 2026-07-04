package com.sethy.easypay.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.Wallet
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.SurfaceCard
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.ButtonTextLink
import com.sethy.easypay.ui.state.OnboardingEffect
import com.sethy.easypay.ui.state.OnboardingEvent
import com.sethy.easypay.ui.viewmodel.OnboardingViewModel

val DefaultOnboardingPages: List<OnboardingPage> = listOf(
    OnboardingPage(
        icon = Lucide.Sparkles,
        title = "Welcome to EasyPay",
        body = "Pay anyone, anywhere — straight from your phone."
    ),
    OnboardingPage(
        icon = Lucide.Wallet,
        title = "All your money in one place",
        body = "Check balances, send money, and track every transaction."
    ),
    OnboardingPage(
        icon = Lucide.Gift,
        title = "Earn rewards on every spend",
        body = "Get cashback and rewards when you shop, send, or save with EasyPay."
    )
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    pages: List<OnboardingPage> = DefaultOnboardingPages,
    onNavigateToLogin: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val totalSteps = pages.size

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = EasyPaySpacing.md),
                horizontalArrangement = Arrangement.End
            ) {
                ButtonTextLink(
                    text = "Skip",
                    onClick = { viewModel.onEvent(OnboardingEvent.Skip) },
                    contentColor = Muted
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    val forward = targetState > initialState
                    val direction = if (forward) 1 else -1
                    (slideInHorizontally(tween(300)) { fullWidth -> direction * fullWidth / 4 } + fadeIn(tween(300)))
                        .togetherWith(
                            slideOutHorizontally(tween(300)) { fullWidth -> -direction * fullWidth / 4 } + fadeOut(tween(300))
                        )
                },
                label = "onboardingPage",
                modifier = Modifier.weight(1f)
            ) { step ->
                OnboardingPageContent(page = pages[step])
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

            PageIndicator(
                currentStep = state.currentStep,
                totalSteps = totalSteps,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            ButtonPrimary(
                text = if (state.currentStep == totalSteps - 1) "Get started" else "Next",
                onClick = { viewModel.onEvent(OnboardingEvent.Next) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = EasyPayTypography.bodyMD,
                    color = Muted
                )
                ButtonTextLink(
                    text = "Sign in",
                    onClick = onSignInClick,
                    contentColor = Primary
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))
        }
    }
}

@Composable
private fun PageIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val active = index == currentStep
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .then(
                        if (active) Modifier
                            .width(28.dp)
                            .clip(CircleShape)
                            .background(Primary)
                        else Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard)
                    )
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    EasyPayTheme {
        OnboardingScreen(
            onNavigateToLogin = {},
            onSignInClick = {}
        )
    }
}