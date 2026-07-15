package com.sethy.easypay.data.mapper

import com.sethy.easypay.data.dto.AuthResponse
import com.sethy.easypay.data.dto.UserDto
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.AuthResult

fun UserDto.toUser(balance: Double = 0.0): User = User(
    id = id,
    name = name,
    email = email,
    phone = phone,
    balance = balance
)

fun AuthResponse.toAuthResult(): AuthResult = AuthResult(
    user = user.toUser(),
    accessToken = token
)
