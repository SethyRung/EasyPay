package com.sethy.easypay.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sethy.easypay.R
import com.sethy.easypay.ui.components.PrimaryButton
import com.sethy.easypay.ui.components.SecondaryButton

@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageAlpha = remember { Animatable(0f) }
    val imageTranslationY = remember { Animatable(50f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        imageAlpha.animateTo(1f, animationSpec = tween(600))
        imageTranslationY.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))

        titleAlpha.animateTo(1f, animationSpec = tween(500))

        subtitleAlpha.animateTo(1f, animationSpec = tween(500))

        buttonAlpha.animateTo(1f, animationSpec = tween(500))
    }

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
                        .height(280.dp)
                        .graphicsLayer {
                            alpha = imageAlpha.value
                            translationY = imageTranslationY.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.onboarding),
                        contentDescription = "Onboarding Illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Easy Online Payment",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = titleAlpha.value }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Make your payment experience better today. No additional admin fees.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .graphicsLayer { alpha = subtitleAlpha.value }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .graphicsLayer { alpha = buttonAlpha.value }
            ) {
                PrimaryButton(
                    text = "Login",
                    onClick = onLoginClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SecondaryButton(
                    text = "Sign Up",
                    onClick = onSignUpClick
                )
            }
        }
    }
}
