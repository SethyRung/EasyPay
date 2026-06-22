package com.sethy.easypay.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.sethy.easypay.design.components.BrandMark
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.ButtonTextLink
import com.sethy.easypay.design.components.TextInput
import com.sethy.easypay.design.components.TopNav
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
        topBar = {
            TopNav(
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md, Alignment.CenterVertically)
        ) {
            BrandMark(size = 72.dp)
            Spacer(modifier = Modifier.height(EasyPaySpacing.md))
            Text(
                text = "Welcome back",
                style = EasyPayTypography.displayMD,
                color = Ink
            )
            Text(
                text = "Sign in to your account",
                style = EasyPayTypography.bodyMD,
                color = Muted
            )
            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))

            TextInput(
                value = state.email,
                onValueChange = { viewModel.onLoginEvent(LoginEvent.EmailChanged(it)) },
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

            TextInput(
                value = state.password,
                onValueChange = { viewModel.onLoginEvent(LoginEvent.PasswordChanged(it)) },
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
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ButtonTextLink(
                    text = "Forgot password?",
                    onClick = { /* TODO */ }
                )
            }

            state.errorMessage?.let {
                Text(
                    text = it,
                    style = EasyPayTypography.caption,
                    color = com.sethy.easypay.design.Error
                )
            }

            Spacer(modifier = Modifier.height(EasyPaySpacing.md))
            ButtonPrimary(
                text = "Sign In",
                onClick = { viewModel.onLoginEvent(LoginEvent.Submit) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Don't have an account? ",
                    style = EasyPayTypography.bodyMD,
                    color = Muted
                )
                ButtonTextLink(
                    text = "Sign up",
                    onClick = onSignupClick
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
