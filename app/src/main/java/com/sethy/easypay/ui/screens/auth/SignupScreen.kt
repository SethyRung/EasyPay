package com.sethy.easypay.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.sethy.easypay.data.model.User
import com.sethy.easypay.ui.components.AppTextField
import com.sethy.easypay.ui.components.PrimaryButton
import com.sethy.easypay.ui.viewmodel.AuthViewModel
import com.sethy.easypay.ui.theme.Success
import com.sethy.easypay.ui.theme.TextSecondary
import com.sethy.easypay.util.ValidationUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    viewModel: AuthViewModel,
    onSignupSuccess: (User) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val signupState by viewModel.signupState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }
    var obscurePassword by remember { mutableStateOf(true) }
    var acceptTerms by remember { mutableStateOf(false) }
    var attemptedNext by remember { mutableStateOf(false) }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(100)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    val stepIndicatorAlpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 1f else 0f,
        animationSpec = tween(500),
        label = "stepIndicatorAlpha"
    )

    fun handleBack() {
        if (currentStep > 0) {
            currentStep--
            attemptedNext = false
        } else {
            onBackClick()
        }
    }

    fun handleNext() {
        if (currentStep < 2) {
            attemptedNext = true
            if (viewModel.validateSignupStep(currentStep)) {
                currentStep++
                attemptedNext = false
            }
        } else {
            if (!acceptTerms) return
            viewModel.signup(onSuccess = onSignupSuccess)
        }
    }

    val isStepValid = if (attemptedNext) {
        viewModel.isSignupStepValid(currentStep)
    } else true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column {
                    PrimaryButton(
                        text = if (currentStep == 2) "Create Account" else "Continue",
                        onClick = { handleNext() },
                        enabled = !signupState.isLoading && (currentStep != 2 || acceptTerms),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
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
            AnimatedStepIndicator(
                currentStep = currentStep,
                totalSteps = 3,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .alpha(stepIndicatorAlpha)
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeIn(animationSpec = tween(250)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { -it / 2 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeOut(animationSpec = tween(250))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeIn(animationSpec = tween(250)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { it / 2 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                        ) + fadeOut(animationSpec = tween(250))
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
private fun AnimatedStepIndicator(
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
            AnimatedStepCircle(
                index = i,
                isActive = i == currentStep,
                isCompleted = i < currentStep
            )

            if (i < totalSteps - 1) {
                AnimatedStepConnector(isActive = i < currentStep)
            }
        }
    }
}

@Composable
private fun AnimatedStepCircle(
    index: Int,
    isActive: Boolean,
    isCompleted: Boolean
) {
    val targetScale = if (isActive) 1.15f else 1f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "stepScale"
    )

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
            .size(32.dp)
            .background(backgroundColor, CircleShape)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Lucide.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
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
private fun RowScope.AnimatedStepConnector(isActive: Boolean) {
    val targetWidth = if (isActive) 1f else 0f
    val progress by animateFloatAsState(
        targetValue = targetWidth,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "connectorProgress"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .height(3.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun StepOne(
    viewModel: AuthViewModel,
    signupState: com.sethy.easypay.ui.viewmodel.SignupUiState
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    val offset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 0f else 60f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "stepOneOffset"
    )
    val alpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 1f else 0f,
        animationSpec = tween(500),
        label = "stepOneAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .offset(y = offset.dp)
            .alpha(alpha)
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
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            value = signupState.name,
            onValueChange = viewModel::updateSignupName,
            label = "Full Name",
            placeholder = "John Doe",
            isError = signupState.nameError != null,
            errorMessage = signupState.nameError,
            imeAction = ImeAction.Next,
            onImeAction = { viewModel.touchSignupName() },
            leadingIcon = {
                Icon(
                    imageVector = Lucide.User,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            imeAction = ImeAction.Done,
            onImeAction = { viewModel.touchSignupEmail() },
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Mail,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    val offset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 0f else 60f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "stepTwoOffset"
    )
    val alpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 1f else 0f,
        animationSpec = tween(500),
        label = "stepTwoAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .offset(y = offset.dp)
            .alpha(alpha)
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
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            value = signupState.phone,
            onValueChange = viewModel::updateSignupPhone,
            label = "Phone Number",
            placeholder = "+1 234 567 890",
            isError = signupState.phoneError != null,
            errorMessage = signupState.phoneError,
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
            onImeAction = { viewModel.touchSignupPhone() },
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            imeAction = ImeAction.Next,
            onImeAction = { viewModel.touchSignupPassword() },
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggleObscure) {
                    Icon(
                        imageVector = if (obscurePassword) Lucide.EyeOff else Lucide.Eye,
                        contentDescription = if (obscurePassword) "Show password" else "Hide password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        if (signupState.password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            PasswordStrengthBar(
                strength = signupState.passwordStrength,
                requirements = signupState.passwordRequirements
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = signupState.confirmPassword,
            onValueChange = viewModel::updateSignupConfirmPassword,
            label = "Confirm Password",
            placeholder = "••••••••",
            isError = signupState.confirmPasswordError != null,
            errorMessage = signupState.confirmPasswordError,
            visualTransformation = if (obscureConfirmPassword) PasswordVisualTransformation() else VisualTransformation.None,
            imeAction = ImeAction.Done,
            onImeAction = { viewModel.touchSignupConfirmPassword() },
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                IconButton(onClick = { obscureConfirmPassword = !obscureConfirmPassword }) {
                    Icon(
                        imageVector = if (obscureConfirmPassword) Lucide.EyeOff else Lucide.Eye,
                        contentDescription = if (obscureConfirmPassword) "Show password" else "Hide password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
private fun PasswordStrengthBar(
    strength: ValidationUtils.PasswordStrength,
    requirements: ValidationUtils.PasswordRequirements
) {
    val (trackColor, strengthLabel) = when (strength) {
        ValidationUtils.PasswordStrength.WEAK -> Color(0xFFFF6B6B) to "Weak"
        ValidationUtils.PasswordStrength.MEDIUM -> Color(0xFFFFA502) to "Medium"
        ValidationUtils.PasswordStrength.STRONG -> Success to "Strong"
    }

    val progress = when (strength) {
        ValidationUtils.PasswordStrength.WEAK -> 0.33f
        ValidationUtils.PasswordStrength.MEDIUM -> 0.66f
        ValidationUtils.PasswordStrength.STRONG -> 1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "strengthProgress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(2.dp))
                        .background(trackColor)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = strengthLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = trackColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        PasswordRequirementItem(
            label = "At least 8 characters",
            met = requirements.minLength
        )
        PasswordRequirementItem(
            label = "One uppercase letter",
            met = requirements.hasUppercase
        )
        PasswordRequirementItem(
            label = "One lowercase letter",
            met = requirements.hasLowercase
        )
        PasswordRequirementItem(
            label = "One number",
            met = requirements.hasNumber
        )
    }
}

@Composable
private fun PasswordRequirementItem(
    label: String,
    met: Boolean
) {
    val color = if (met) Success else TextSecondary
    val icon = if (met) Lucide.Check else Lucide.Circle

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
private fun StepThree(
    acceptTerms: Boolean,
    onAcceptTermsChange: (Boolean) -> Unit
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    val offset by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 0f else 60f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "stepThreeOffset"
    )
    val alpha by animateFloatAsState(
        targetValue = if (animProgress.value > 0.1f) 1f else 0f,
        animationSpec = tween(500),
        label = "stepThreeAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .offset(y = offset.dp)
            .alpha(alpha)
    ) {
        Text(
            text = "Almost there!",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Review and accept our terms to finish.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = onAcceptTermsChange
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = buildAnnotatedString {
                        append("I agree to the ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)) {
                            append("Terms of Service")
                        }
                        append(" and ")
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)) {
                            append("Privacy Policy")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
