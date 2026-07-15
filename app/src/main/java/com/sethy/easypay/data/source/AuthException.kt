package com.sethy.easypay.data.source

/**
 * The default message is identical for unknown emails and wrong passwords
 * so callers cannot use it to enumerate registered emails.
 */
class InvalidCredentialsException(
    message: String = "Invalid email or password"
) : Exception(message)

class UserAlreadyExistsException(
    message: String = "An account with this email already exists"
) : Exception(message)