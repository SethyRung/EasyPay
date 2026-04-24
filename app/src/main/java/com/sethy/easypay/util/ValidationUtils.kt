package com.sethy.easypay.util

import android.util.Patterns

object ValidationUtils {

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            name.length > 50 -> "Name must be less than 50 characters"
            !name.all { it.isLetter() || it.isWhitespace() } -> "Name can only contain letters and spaces"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
            else -> null
        }
    }

    fun validatePhone(phone: String): String? {
        val digitsOnly = phone.filter { it.isDigit() }
        return when {
            phone.isBlank() -> "Phone number is required"
            digitsOnly.length < 7 -> "Phone number is too short"
            digitsOnly.length > 15 -> "Phone number is too long"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain an uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain a lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain a number"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }

    fun calculatePasswordStrength(password: String): PasswordStrength {
        if (password.length < 8) return PasswordStrength.WEAK

        var score = 0
        if (password.length >= 12) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when (score) {
            0, 1 -> PasswordStrength.WEAK
            2, 3 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }

    enum class PasswordStrength {
        WEAK, MEDIUM, STRONG
    }

    data class PasswordRequirements(
        val minLength: Boolean = false,
        val hasUppercase: Boolean = false,
        val hasLowercase: Boolean = false,
        val hasNumber: Boolean = false
    ) {
        fun allMet(): Boolean = minLength && hasUppercase && hasLowercase && hasNumber
    }

    fun checkPasswordRequirements(password: String): PasswordRequirements {
        return PasswordRequirements(
            minLength = password.length >= 8,
            hasUppercase = password.any { it.isUpperCase() },
            hasLowercase = password.any { it.isLowerCase() },
            hasNumber = password.any { it.isDigit() }
        )
    }
}
