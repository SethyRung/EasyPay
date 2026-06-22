package com.sethy.easypay.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.composables.icons.lucide.Phone
import com.sethy.easypay.R
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.ButtonTextLink
import com.sethy.easypay.design.components.EasyPayWordmark
import com.sethy.easypay.design.components.PasswordStrengthBar
import com.sethy.easypay.design.components.TextInput
import com.sethy.easypay.ui.state.SignupEffect
import com.sethy.easypay.ui.state.SignupEvent
import com.sethy.easypay.ui.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit,
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
                is SignupEffect.ShowError -> { /* Handled via state.errorMessage */ }
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
                .padding(horizontal = EasyPaySpacing.xl, vertical = EasyPaySpacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(EasyPaySpacing.xxl))

            Text(
                text = "Create\nAccount",
                style = EasyPayTypography.displayMD.copy(fontWeight = FontWeight.SemiBold),
                color = Ink
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.sm))

            Text(
                text = "Enter your details to create your account",
                style = EasyPayTypography.bodyMD,
                color = Muted
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            TextInput(
                value = state.name,
                onValueChange = { viewModel.onSignupEvent(SignupEvent.NameChanged(it)) },
                label = "Full name",
                isError = state.nameError != null,
                errorMessage = state.nameError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            TextInput(
                value = state.email,
                onValueChange = { viewModel.onSignupEvent(SignupEvent.EmailChanged(it)) },
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

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            TextInput(
                value = state.phone,
                onValueChange = { viewModel.onSignupEvent(SignupEvent.PhoneChanged(it)) },
                label = "Phone number",
                isError = state.phoneError != null,
                errorMessage = state.phoneError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            TextInput(
                value = state.password,
                onValueChange = { viewModel.onSignupEvent(SignupEvent.PasswordChanged(it)) },
                label = "Password",
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
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

            Spacer(modifier = Modifier.height(EasyPaySpacing.xs))

            PasswordStrengthBar(
                strength = state.passwordStrength,
                requirements = state.passwordRequirements,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            TextInput(
                value = state.confirmPassword,
                onValueChange = { viewModel.onSignupEvent(SignupEvent.ConfirmPasswordChanged(it)) },
                label = "Confirm password",
                isError = state.confirmPasswordError != null,
                errorMessage = state.confirmPasswordError,
                visualTransformation = if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
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

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.termsAccepted,
                    onCheckedChange = {
                        viewModel.onSignupEvent(SignupEvent.TermsAcceptedChanged(it))
                    }
                )
                Text(
                    text = "I agree to the Terms and Privacy Policy",
                    style = EasyPayTypography.bodySM,
                    color = Muted
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

            ButtonPrimary(
                text = "Create Account",
                onClick = { viewModel.onSignupEvent(SignupEvent.Submit) },
                enabled = !state.isLoading,
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
                    text = "Sign In",
                    onClick = onLoginClick,
                    contentColor = Primary
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            OrDivider(text = "Or sign up with")

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
            ) {
                SocialButton(
                    text = "Google",
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_g),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
            }

            state.errorMessage?.let {
                Spacer(modifier = Modifier.height(EasyPaySpacing.md))
                Text(
                    text = it,
                    style = EasyPayTypography.caption,
                    color = Error
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))
        }
    }
}

@Preview
@Composable
private fun SignupScreenPreview() {
    EasyPayTheme {
        SignupScreen(
            onSignupSuccess = {},
            onLoginClick = {},
            onBackClick = {}
        )
    }
}