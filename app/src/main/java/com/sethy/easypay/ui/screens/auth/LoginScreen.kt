package com.sethy.easypay.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.sethy.easypay.R
import com.sethy.easypay.data.model.User
import com.sethy.easypay.ui.components.AppTextField
import com.sethy.easypay.ui.components.PrimaryButton
import com.sethy.easypay.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (User) -> Unit,
    onSignupClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loginState by viewModel.loginState.collectAsState()
    var obscurePassword by remember { mutableStateOf(true) }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(100)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
    }

    val logoScale by animateFloatAsState(
        targetValue = if (animProgress.value > 0.05f) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.05f) 1f else 0f,
        animationSpec = tween(500),
        label = "logoAlpha"
    )

    val titleOffset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.2f) 0f else 50f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "titleOffset"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.2f) 1f else 0f,
        animationSpec = tween(500),
        label = "titleAlpha"
    )

    val subtitleOffset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.3f) 0f else 40f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "subtitleOffset"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.3f) 1f else 0f,
        animationSpec = tween(500),
        label = "subtitleAlpha"
    )

    val fieldOffset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.4f) 0f else 60f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "fieldOffset"
    )
    val fieldAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.35f) 1f else 0f,
        animationSpec = tween(500),
        label = "fieldAlpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (animProgress.value > 0.6f) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "buttonScale"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.6f) 1f else 0f,
        animationSpec = tween(400),
        label = "buttonAlpha"
    )

    fun handleSignIn() {
        viewModel.login(onSuccess = onLoginSuccess)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "EasyPay Logo",
                    modifier = Modifier
                        .size(72.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .offset(y = titleOffset.dp)
                        .alpha(titleAlpha)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign in to your account",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .offset(y = subtitleOffset.dp)
                        .alpha(subtitleAlpha)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = fieldOffset.dp)
                        .alpha(fieldAlpha)
                ) {
                    AppTextField(
                        value = loginState.email,
                        onValueChange = viewModel::updateLoginEmail,
                        label = "Email",
                        placeholder = "you@example.com",
                        isError = loginState.emailError != null,
                        errorMessage = loginState.emailError,
                        keyboardType = KeyboardType.Email,
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Mail,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = loginState.password,
                        onValueChange = viewModel::updateLoginPassword,
                        label = "Password",
                        placeholder = "••••••••",
                        isError = loginState.passwordError != null,
                        errorMessage = loginState.passwordError,
                        visualTransformation = if (obscurePassword) PasswordVisualTransformation() else VisualTransformation.None,
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { obscurePassword = !obscurePassword }) {
                                Icon(
                                    imageVector = if (obscurePassword) Lucide.EyeOff else Lucide.Eye,
                                    contentDescription = if (obscurePassword) "Show password" else "Hide password",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { /* TODO: Forgot password */ }) {
                            Text(
                                "Forgot password?",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .scale(buttonScale)
                            .alpha(buttonAlpha)
                    ) {
                        PrimaryButton(
                            text = "Sign In",
                            onClick = { handleSignIn() },
                            enabled = !loginState.isLoading
                        )
                    }

                    loginState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .offset(y = fieldOffset.dp)
                        .alpha(fieldAlpha),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = onSignupClick,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            "Sign up",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
