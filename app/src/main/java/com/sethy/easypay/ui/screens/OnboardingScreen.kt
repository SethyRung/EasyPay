package com.sethy.easypay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.sethy.easypay.ui.components.PrimaryButton
import com.sethy.easypay.ui.components.SecondaryButton
import com.sethy.easypay.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/logo.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "EasyPay Logo",
                            modifier = Modifier
                                .size(24.dp),
                            contentScale = ContentScale.Fit
                        )

                        Text(text = "EasyPay", style = AppTypography.headlineSmall)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement =  Arrangement.SpaceBetween,
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/onboarding.svg")
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "Onboarding Illustration",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
               modifier = Modifier.background(color = Color.White)
                   .fillMaxWidth()
                   .padding(horizontal = 16.dp, vertical = 32.dp),
            ) {
                Text(
                    text = "Easy Online Payment",
                    style = AppTypography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Make your payment experience more better today. No additional admin fee",
                    style = AppTypography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(48.dp))

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