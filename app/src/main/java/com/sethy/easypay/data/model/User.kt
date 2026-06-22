package com.sethy.easypay.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val balance: Double = 0.0,
    val avatarUrl: String? = null
)
