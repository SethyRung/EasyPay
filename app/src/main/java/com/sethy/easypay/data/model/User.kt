package com.sethy.easypay.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val balance: Double,
    val avatarUrl: String? = null
)
