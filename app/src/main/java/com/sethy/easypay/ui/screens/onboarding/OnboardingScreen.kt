package com.sethy.easypay.ui.screens.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.components.BrandMark
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.ButtonTextLink
import com.sethy.easypay.design.components.EasyPayWordmark
import com.sethy.easypay.design.EasyPayTheme
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.97f,
        animationSpec = tween(600),
        label = "onboardingScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "onboardingAlpha"
    )

    Scaffold(
        modifier = modifier,
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl)
                .scale(scale)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(EasyPaySpacing.xxl))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BrandMark(size = 64.dp)
                Spacer(modifier = Modifier.height(EasyPaySpacing.md))
                EasyPayWordmark()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
            ) {
                Text(
                    text = "Make every payment effortless.",
                    style = EasyPayTypography.displayLG,
                    color = Muted
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
            ) {
                ButtonPrimary(
                    text = "Get started",
                    onClick = onGetStartedClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
                ButtonTextLink(
                    text = "I already have an account",
                    onClick = onLoginClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    EasyPayTheme {
        OnboardingScreen(
            onGetStartedClick = {},
            onLoginClick = {}
        )
    }
}
