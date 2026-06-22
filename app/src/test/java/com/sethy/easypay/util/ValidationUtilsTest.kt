package com.sethy.easypay.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    // ─── validateName ────────────────────────────────────────────────────────

    @Test
    fun `validateName returns null for valid name`() {
        assertNull(ValidationUtils.validateName("Alice"))
        assertNull(ValidationUtils.validateName("Bob Smith"))
    }

    @Test
    fun `validateName returns error for blank name`() {
        assertEquals(null, "Name is required", ValidationUtils.validateName(""))
        assertEquals(null, "Name is required", ValidationUtils.validateName("   "))
    }

    @Test
    fun `validateName returns error for name too short`() {
        assertEquals(null, "Name must be at least 2 characters", ValidationUtils.validateName("A"))
    }

    @Test
    fun `validateName returns error for name too long`() {
        assertEquals(null, "Name must be less than 50 characters", ValidationUtils.validateName("A".repeat(51)))
    }

    @Test
    fun `validateName returns error for name with digits`() {
        assertEquals(null, "Name can only contain letters and spaces", ValidationUtils.validateName("Alice123"))
        assertEquals(null, "Name can only contain letters and spaces", ValidationUtils.validateName("Bob@Smith"))
    }

    // ─── validateEmail ───────────────────────────────────────────────────────

    @Test
    fun `validateEmail returns null for valid email`() {
        assertNull(ValidationUtils.validateEmail("alice@example.com"))
        assertNull(ValidationUtils.validateEmail("bob.smith@company.org"))
    }

    @Test
    fun `validateEmail returns error for blank email`() {
        assertEquals(null, "Email is required", ValidationUtils.validateEmail(""))
        assertEquals(null, "Email is required", ValidationUtils.validateEmail("   "))
    }

    @Test
    fun `validateEmail returns error for invalid format`() {
        assertEquals(null, "Enter a valid email address", ValidationUtils.validateEmail("notanemail"))
        assertEquals(null, "Enter a valid email address", ValidationUtils.validateEmail("alice@"))
        assertEquals(null, "Enter a valid email address", ValidationUtils.validateEmail("@example.com"))
    }

    // ─── validatePhone ───────────────────────────────────────────────────────

    @Test
    fun `validatePhone returns null for valid phone`() {
        assertNull(ValidationUtils.validatePhone("1234567890"))
        assertNull(ValidationUtils.validatePhone("+1 234 567 8901"))
        assertNull(ValidationUtils.validatePhone("(123) 456-7890"))
    }

    @Test
    fun `validatePhone returns error for blank phone`() {
        assertEquals(null, "Phone number is required", ValidationUtils.validatePhone(""))
        assertEquals(null, "Phone number is required", ValidationUtils.validatePhone("   "))
    }

    @Test
    fun `validatePhone returns error for phone too short`() {
        assertEquals(null, "Phone number is too short", ValidationUtils.validatePhone("123456"))
    }

    @Test
    fun `validatePhone returns error for phone too long`() {
        assertEquals(null, "Phone number is too long", ValidationUtils.validatePhone("1".repeat(16)))
    }

    // ─── validatePassword ────────────────────────────────────────────────────

    @Test
    fun `validatePassword returns null for valid password`() {
        assertNull(ValidationUtils.validatePassword("Password1"))
        assertNull(ValidationUtils.validatePassword("SecureP@ssw0rd"))
    }

    @Test
    fun `validatePassword returns error for blank password`() {
        assertEquals(null, "Password is required", ValidationUtils.validatePassword(""))
    }

    @Test
    fun `validatePassword returns error for password too short`() {
        assertEquals(null, "Password must be at least 8 characters", ValidationUtils.validatePassword("Pass1"))
    }

    @Test
    fun `validatePassword returns error for missing uppercase`() {
        assertEquals(null, "Password must contain an uppercase letter", ValidationUtils.validatePassword("password1"))
    }

    @Test
    fun `validatePassword returns error for missing lowercase`() {
        assertEquals(null, "Password must contain a lowercase letter", ValidationUtils.validatePassword("PASSWORD1"))
    }

    @Test
    fun `validatePassword returns error for missing number`() {
        assertEquals(null, "Password must contain a number", ValidationUtils.validatePassword("Password"))
    }

    // ─── validateConfirmPassword ────────────────────────────────────────────

    @Test
    fun `validateConfirmPassword returns null when passwords match`() {
        assertNull(ValidationUtils.validateConfirmPassword("Password1", "Password1"))
    }

    @Test
    fun `validateConfirmPassword returns error for blank confirmation`() {
        assertEquals(null, "Please confirm your password", ValidationUtils.validateConfirmPassword("Password1", ""))
    }

    @Test
    fun `validateConfirmPassword returns error when passwords do not match`() {
        assertEquals(null, "Passwords do not match", ValidationUtils.validateConfirmPassword("Password1", "Password2"))
    }

    // ─── calculatePasswordStrength ─────────────────────────────────────────

    @Test
    fun `calculatePasswordStrength returns WEAK for short password`() {
        assertEquals(null, ValidationUtils.PasswordStrength.WEAK, ValidationUtils.calculatePasswordStrength("Pass1"))
    }

    @Test
    fun `calculatePasswordStrength returns MEDIUM for medium-low score`() {
        assertEquals(null, ValidationUtils.PasswordStrength.MEDIUM, ValidationUtils.calculatePasswordStrength("password1"))
    }

    @Test
    fun `calculatePasswordStrength returns MEDIUM for medium score`() {
        assertEquals(null, ValidationUtils.PasswordStrength.MEDIUM, ValidationUtils.calculatePasswordStrength("Password1"))
    }

    @Test
    fun `calculatePasswordStrength returns STRONG for high score`() {
        assertEquals(null, ValidationUtils.PasswordStrength.STRONG, ValidationUtils.calculatePasswordStrength("Password1@"))
        assertEquals(null, ValidationUtils.PasswordStrength.STRONG, ValidationUtils.calculatePasswordStrength("SecureP@ssw0rd!"))
    }

    // ─── checkPasswordRequirements ─────────────────────────────────────────

    @Test
    fun `checkPasswordRequirements returns correct flags`() {
        val reqs = ValidationUtils.checkPasswordRequirements("Password1")
        assertTrue(reqs.minLength)
        assertTrue(reqs.hasUppercase)
        assertTrue(reqs.hasLowercase)
        assertTrue(reqs.hasNumber)
        assertTrue(reqs.allMet())

        val weak = ValidationUtils.checkPasswordRequirements("pass")
        assertFalse(weak.minLength)
        assertFalse(weak.hasUppercase)
        assertFalse(weak.allMet())
    }
}