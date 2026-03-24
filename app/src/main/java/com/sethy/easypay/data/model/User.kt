package com.sethy.easypay.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val balance: Double,
    val avatarUrl: String? = null
)
