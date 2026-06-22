package com.sethy.easypay.ui.viewmodel

import com.sethy.easypay.data.model.User
import com.sethy.easypay.domain.usecase.LoginUseCase
import com.sethy.easypay.domain.usecase.RegisterUseCase
import com.sethy.easypay.ui.state.LoginEvent
import com.sethy.easypay.ui.state.SignupEvent
import com.sethy.easypay.ui.state.SignupField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuthViewModelTest {

    private fun createViewModel(): AuthViewModel {
        val loginUseCase: LoginUseCase = mock()
        val registerUseCase: RegisterUseCase = mock()
        return AuthViewModel(loginUseCase, registerUseCase)
    }

    // ─── Login state validation ─────────────────────────────────────────────

    @Test
    fun login_email_validation_fires_on_EmailTouched() = runTest {
        val vm = createViewModel()

        vm.onLoginEvent(LoginEvent.EmailChanged("invalid-email"))
        vm.onLoginEvent(LoginEvent.EmailTouched)

        assertEquals("Enter a valid email address", vm.loginState.value.emailError)
    }

    @Test
    fun login_email_error_clears_when_user_types_valid_email() = runTest {
        val vm = createViewModel()

        vm.onLoginEvent(LoginEvent.EmailChanged("invalid-email"))
        vm.onLoginEvent(LoginEvent.EmailTouched)
        assertEquals("Enter a valid email address", vm.loginState.value.emailError)

        vm.onLoginEvent(LoginEvent.EmailChanged("alice@example.com"))
        assertNull(vm.loginState.value.emailError)
    }

    @Test
    fun login_password_validation_fires_on_PasswordTouched() = runTest {
        val vm = createViewModel()

        vm.onLoginEvent(LoginEvent.PasswordChanged("short"))
        vm.onLoginEvent(LoginEvent.PasswordTouched)

        assertEquals("Password must be at least 8 characters", vm.loginState.value.passwordError)
    }

    @Test
    fun login_shows_error_when_fields_are_invalid() = runTest {
        val vm = createViewModel()

        vm.onLoginEvent(LoginEvent.Submit)

        assertEquals("Email is required", vm.loginState.value.emailError)
        assertEquals("Password must be at least 8 characters", vm.loginState.value.passwordError)
        assertFalse(vm.loginState.value.isLoading)
    }

    // ─── Signup state validation ───────────────────────────────────────────

    @Test
    fun signup_validates_name_on_touch() = runTest {
        val vm = createViewModel()

        vm.onSignupEvent(SignupEvent.NameChanged("A"))
        vm.onSignupEvent(SignupEvent.FieldTouched(SignupField.NAME))

        assertEquals("Name must be at least 2 characters", vm.signupState.value.nameError)
    }

    @Test
    fun signup_password_strength_updates_as_user_types() = runTest {
        val vm = createViewModel()

        vm.onSignupEvent(SignupEvent.PasswordChanged("Pass1"))

        assertTrue(vm.signupState.value.passwordStrength != null)
        assertEquals(
            com.sethy.easypay.design.components.PasswordStrength.WEAK,
            vm.signupState.value.passwordStrength
        )
    }

    @Test
    fun signup_advances_step_when_NextStep_is_valid() = runTest {
        val vm = createViewModel()

        vm.onSignupEvent(SignupEvent.NameChanged("Alice Smith"))
        vm.onSignupEvent(SignupEvent.EmailChanged("alice@example.com"))
        vm.onSignupEvent(SignupEvent.NextStep)

        assertEquals(1, vm.signupState.value.currentStep)
    }

    @Test
    fun signup_does_not_advance_step_when_name_is_invalid() = runTest {
        val vm = createViewModel()

        vm.onSignupEvent(SignupEvent.NameChanged(""))
        vm.onSignupEvent(SignupEvent.EmailChanged("alice@example.com"))
        vm.onSignupEvent(SignupEvent.NextStep)

        assertEquals(0, vm.signupState.value.currentStep)
    }

    @Test
    fun signup_dismissError_clears_error_message() = runTest {
        val vm = createViewModel()

        // Trigger validation error by touching fields with blank values
        vm.onSignupEvent(SignupEvent.FieldTouched(SignupField.NAME))
        assertEquals("Name is required", vm.signupState.value.nameError)

        // DismissError clears error message (set during previous validation)
        vm.onSignupEvent(SignupEvent.DismissError)
        assertNull(vm.signupState.value.errorMessage)
    }
}
