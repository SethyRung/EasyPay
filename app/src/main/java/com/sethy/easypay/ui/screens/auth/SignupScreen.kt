package com.sethy.easypay.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.PasswordStrengthBar
import com.sethy.easypay.design.components.StepIndicator
import com.sethy.easypay.design.components.TextInput
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.state.SignupEffect
import com.sethy.easypay.ui.state.SignupEvent
import com.sethy.easypay.ui.state.SignupField
import com.sethy.easypay.ui.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSignupSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.signupState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.signupEffect.collect { effect ->
            when (effect) {
                SignupEffect.NavigateToHome -> onSignupSuccess()
                SignupEffect.NavigateBack -> onBackClick()
                is SignupEffect.ShowError -> { /* Handled via state.errorMessage */ }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                title = "Create Account",
                showBackButton = true,
                onBackClick = { viewModel.onSignupEvent(SignupEvent.PreviousStep) }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 0.dp) {
                ButtonPrimary(
                    text = if (state.currentStep < 2) "Continue" else "Create account",
                    onClick = {
                        if (state.currentStep < 2) {
                            viewModel.onSignupEvent(SignupEvent.NextStep)
                        } else {
                            viewModel.onSignupEvent(SignupEvent.Submit)
                        }
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(EasyPaySpacing.md)
                )
            }
        },
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.lg, Alignment.Top)
        ) {
            StepIndicator(
                currentStep = state.currentStep,
                totalSteps = 3,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedContent(
                targetState = state.currentStep,
                label = "signupStep",
                transitionSpec = {
                    slideIntoContainer(androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left) togetherWith
                        slideOutOfContainer(androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left)
                }
            ) { step ->
                when (step) {
                    0 -> StepOne(
                        state = state,
                        onEvent = viewModel::onSignupEvent
                    )
                    1 -> StepTwo(
                        state = state,
                        onEvent = viewModel::onSignupEvent,
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityChange = { passwordVisible = it },
                        confirmPasswordVisible = confirmPasswordVisible,
                        onConfirmPasswordVisibilityChange = { confirmPasswordVisible = it }
                    )
                    2 -> StepThree(
                        state = state,
                        onEvent = viewModel::onSignupEvent
                    )
                }
            }

            state.errorMessage?.let {
                Text(
                    text = it,
                    style = EasyPayTypography.caption,
                    color = com.sethy.easypay.design.Error
                )
            }
        }
    }
}

@Composable
private fun StepOne(
    state: com.sethy.easypay.ui.state.SignupUiState,
    onEvent: (SignupEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md),
        modifier = Modifier.fillMaxWidth()
    ) {
        TextInput(
            value = state.name,
            onValueChange = { onEvent(SignupEvent.NameChanged(it)) },
            label = "Full name",
            isError = state.nameError != null,
            errorMessage = state.nameError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        TextInput(
            value = state.email,
            onValueChange = { onEvent(SignupEvent.EmailChanged(it)) },
            label = "Email",
            placeholder = "you@example.com",
            isError = state.emailError != null,
            errorMessage = state.emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StepTwo(
    state: com.sethy.easypay.ui.state.SignupUiState,
    onEvent: (SignupEvent) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md),
        modifier = Modifier.fillMaxWidth()
    ) {
        TextInput(
            value = state.phone,
            onValueChange = { onEvent(SignupEvent.PhoneChanged(it)) },
            label = "Phone number",
            isError = state.phoneError != null,
            errorMessage = state.phoneError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        TextInput(
            value = state.password,
            onValueChange = { onEvent(SignupEvent.PasswordChanged(it)) },
            label = "Password",
            isError = state.passwordError != null,
            errorMessage = state.passwordError,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Lucide.EyeOff else Lucide.Eye,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        TextInput(
            value = state.confirmPassword,
            onValueChange = { onEvent(SignupEvent.ConfirmPasswordChanged(it)) },
            label = "Confirm password",
            isError = state.confirmPasswordError != null,
            errorMessage = state.confirmPasswordError,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onConfirmPasswordVisibilityChange(!confirmPasswordVisible) }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Lucide.EyeOff else Lucide.Eye,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        PasswordStrengthBar(
            strength = state.passwordStrength,
            requirements = state.passwordRequirements,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StepThree(
    state: com.sethy.easypay.ui.state.SignupUiState,
    onEvent: (SignupEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Review your information",
            style = EasyPayTypography.titleMD,
            color = Ink
        )
        Text(
            text = "Name: ${state.name}",
            style = EasyPayTypography.bodyMD,
            color = Ink
        )
        Text(
            text = "Email: ${state.email}",
            style = EasyPayTypography.bodyMD,
            color = Ink
        )
        Text(
            text = "Phone: ${state.phone}",
            style = EasyPayTypography.bodyMD,
            color = Ink
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.termsAccepted,
                onCheckedChange = { onEvent(SignupEvent.TermsAcceptedChanged(it)) }
            )
            Text(
                text = "I agree to the Terms and Privacy Policy",
                style = EasyPayTypography.bodySM,
                color = Muted
            )
        }
    }
}

@Preview
@Composable
private fun SignupScreenPreview() {
    EasyPayTheme {
        SignupScreen(
            onSignupSuccess = {},
            onBackClick = {}
        )
    }
}
