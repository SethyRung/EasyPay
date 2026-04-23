package com.sethy.easypay.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sethy.easypay.R
import com.sethy.easypay.ui.components.PrimaryButton
import com.sethy.easypay.ui.components.SecondaryButton
import com.sethy.easypay.ui.theme.TextPrimary
import com.sethy.easypay.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    val imageScale by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "imageScale"
    )
    val imageAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 1f else 0f,
        animationSpec = tween(700),
        label = "imageAlpha"
    )

    val titleOffset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.3f) 0f else 60f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "titleOffset"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.3f) 1f else 0f,
        animationSpec = tween(600),
        label = "titleAlpha"
    )

    val subtitleOffset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.45f) 0f else 50f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "subtitleOffset"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.45f) 1f else 0f,
        animationSpec = tween(600),
        label = "subtitleAlpha"
    )

    val buttonOffset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.6f) 0f else 80f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "buttonOffset"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.6f) 1f else 0f,
        animationSpec = tween(500),
        label = "buttonAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .scale(imageScale)
                        .alpha(imageAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.onboarding),
                        contentDescription = "Onboarding Illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Easy Online Payment",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = titleOffset.dp)
                        .alpha(titleAlpha)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Make your payment experience better today. No additional admin fees.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = subtitleOffset.dp)
                        .alpha(subtitleAlpha)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .offset(y = buttonOffset.dp)
                    .alpha(buttonAlpha)
            ) {
                PrimaryButton(
                    text = "Login",
                    onClick = onLoginClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecondaryButton(
                    text = "Sign Up",
                    onClick = onSignUpClick
                )
            }
        }
    }
}
