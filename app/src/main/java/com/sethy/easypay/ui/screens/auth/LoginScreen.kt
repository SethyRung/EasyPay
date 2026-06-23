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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.sethy.easypay.design.components.TextInput
import com.sethy.easypay.ui.state.LoginEffect
import com.sethy.easypay.ui.state.LoginEvent
import com.sethy.easypay.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loginEffect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome -> onLoginSuccess()
                is LoginEffect.ShowError -> { /* Handled via state.errorMessage */ }
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
        ) {
            Spacer(modifier = Modifier.height(EasyPaySpacing.xxl))

            Text(
                text = "Sign in to your\nAccount",
                style = EasyPayTypography.displayMD.copy(fontWeight = FontWeight.SemiBold),
                color = Ink
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.sm))

            Text(
                text = "Enter your email and password to log in to your account",
                style = EasyPayTypography.bodyMD,
                color = Muted
            )

            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            TextInput(
                value = state.email,
                onValueChange = { viewModel.onLoginEvent(LoginEvent.EmailChanged(it)) },
                label = "Email",
                placeholder = "Your mail",
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
                value = state.password,
                onValueChange = { viewModel.onLoginEvent(LoginEvent.PasswordChanged(it)) },
                label = "Password",
                placeholder = "Your Password",
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
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = EasyPaySpacing.xs),
                horizontalArrangement = Arrangement.End
            ) {
                ButtonTextLink(
                    text = "Forgot Password?",
                    onClick = { /* TODO */ },
                    contentColor = Primary
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

            ButtonPrimary(
                text = "Log In",
                onClick = { viewModel.onLoginEvent(LoginEvent.Submit) },
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
                    text = "Don't have an account? ",
                    style = EasyPayTypography.bodyMD,
                    color = Muted
                )
                ButtonTextLink(
                    text = "Sign Up",
                    onClick = onSignupClick,
                    contentColor = Primary
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

            OrDivider(text = "Or login with")

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
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    EasyPayTheme {
        LoginScreen(
            onLoginSuccess = {},
            onSignupClick = {},
            onBackClick = {}
        )
    }
}