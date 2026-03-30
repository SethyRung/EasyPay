package com.sethy.easypay.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.sethy.easypay.data.model.User
import com.sethy.easypay.ui.components.AppTextField
import com.sethy.easypay.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    viewModel: AuthViewModel,
    onSignupSuccess: (User) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val signupState by viewModel.signupState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }
    var obscurePassword by remember { mutableStateOf(true) }
    var acceptTerms by remember { mutableStateOf(false) }
    
    fun handleBack() {
        if (currentStep > 0) {
            currentStep--
        } else {
            onBackClick()
        }
    }
    
    fun handleNext() {
        if (currentStep < 2) {
            currentStep++
        } else {
            if (!acceptTerms) return
            viewModel.signup(onSuccess = onSignupSuccess)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column {
                    Button(
                        onClick = { handleNext() },
                        enabled = !signupState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (signupState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (currentStep == 2) "Create Account" else "Continue")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            StepIndicator(
                currentStep = currentStep,
                totalSteps = 3,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400)
                        ) + fadeIn() togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(400)
                        ) + fadeOut()
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(400)
                        ) + fadeIn() togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(400)
                        ) + fadeOut()
                    }
                },
                label = "step_content"
            ) { step ->
                when (step) {
                    0 -> StepOne(
                        viewModel = viewModel,
                        signupState = signupState
                    )
                    1 -> StepTwo(
                        viewModel = viewModel,
                        signupState = signupState,
                        obscurePassword = obscurePassword,
                        onToggleObscure = { obscurePassword = !obscurePassword }
                    )
                    2 -> StepThree(
                        acceptTerms = acceptTerms,
                        onAcceptTermsChange = { acceptTerms = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            StepCircle(
                index = i,
                isActive = i == currentStep,
                isCompleted = i < currentStep
            )
            
            if (i < totalSteps - 1) {
                StepConnector(isActive = i < currentStep)
            }
        }
    }
}

@Composable
private fun RowScope.StepCircle(
    index: Int,
    isActive: Boolean,
    isCompleted: Boolean
) {
    val backgroundColor = when {
        isActive || isCompleted -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        isActive || isCompleted -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Lucide.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor
            )
        }
    }
}

@Composable
private fun RowScope.StepConnector(isActive: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .background(
                color = if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
    )
}

@Composable
private fun StepOne(
    viewModel: AuthViewModel,
    signupState: com.sethy.easypay.ui.viewmodel.SignupUiState
) {
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(400))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .graphicsLayer { this.alpha = alpha.value }
    ) {
        Text(
            text = "What's your name?",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "We'd love to personalize your experience.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AppTextField(
            value = signupState.name,
            onValueChange = viewModel::updateSignupName,
            label = "Full Name",
            placeholder = "john doe",
            isError = signupState.nameError != null,
            errorMessage = signupState.nameError,
            leadingIcon = {
                Icon(
                    imageVector = Lucide.User,
                    contentDescription = null
                )
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppTextField(
            value = signupState.email,
            onValueChange = viewModel::updateSignupEmail,
            label = "Email",
            placeholder = "you@example.com",
            isError = signupState.emailError != null,
            errorMessage = signupState.emailError,
            keyboardType = KeyboardType.Email,
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Mail,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun StepTwo(
    viewModel: AuthViewModel,
    signupState: com.sethy.easypay.ui.viewmodel.SignupUiState,
    obscurePassword: Boolean,
    onToggleObscure: () -> Unit
) {
    var obscureConfirmPassword by remember { mutableStateOf(true) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(400))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .graphicsLayer { this.alpha = alpha.value }
    ) {
        Text(
            text = "Secure your account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Add your phone number and create a strong password.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AppTextField(
            value = signupState.phone,
            onValueChange = viewModel::updateSignupPhone,
            label = "Phone Number",
            placeholder = "+1234567890",
            isError = signupState.phoneError != null,
            errorMessage = signupState.phoneError,
            keyboardType = KeyboardType.Phone,
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Phone,
                    contentDescription = null
                )
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppTextField(
            value = signupState.password,
            onValueChange = viewModel::updateSignupPassword,
            label = "Password",
            placeholder = "••••••••",
            isError = signupState.passwordError != null,
            errorMessage = signupState.passwordError,
            visualTransformation = if (obscurePassword) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggleObscure) {
                    Icon(
                        imageVector = if (obscurePassword) Lucide.EyeOff else Lucide.Eye,
                        contentDescription = if (obscurePassword) "Show password" else "Hide password"
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppTextField(
            value = signupState.confirmPassword,
            onValueChange = viewModel::updateSignupConfirmPassword,
            label = "Confirm Password",
            placeholder = "••••••••",
            isError = signupState.confirmPasswordError != null,
            errorMessage = signupState.confirmPasswordError,
            visualTransformation = if (obscureConfirmPassword) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { obscureConfirmPassword = !obscureConfirmPassword }) {
                    Icon(
                        imageVector = if (obscureConfirmPassword) Lucide.EyeOff else Lucide.Eye,
                        contentDescription = if (obscureConfirmPassword) "Show password" else "Hide password"
                    )
                }
            }
        )
    }
}

@Composable
private fun StepThree(
    acceptTerms: Boolean,
    onAcceptTermsChange: (Boolean) -> Unit
) {
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(400))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .graphicsLayer { this.alpha = alpha.value }
    ) {
        Text(
            text = "Almost there!",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Review and accept our terms.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAcceptTermsChange(!acceptTerms) },
            color = Color.Transparent,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = onAcceptTermsChange
                )
                
                Text(
                    text = buildAnnotatedString {
                        append("I agree to the ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Terms of Service")
                        }
                        append(" and ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Privacy Policy")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
