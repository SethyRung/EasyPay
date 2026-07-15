package com.sethy.easypay.data.source

import com.sethy.easypay.data.model.User

data class AuthResult(
    val user: User,
    val accessToken: String
)
