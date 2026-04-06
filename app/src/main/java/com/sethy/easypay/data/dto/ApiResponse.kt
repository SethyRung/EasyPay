package com.sethy.easypay.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val status: Status,
    val data: T?,
    val meta: Meta? = null
)

@Serializable
data class Status(
    val code: String,
    val message: String,
    val requestId: String,
    val requestTime: Long
)

@Serializable
data class Meta(
    val total: Int,
    val limit: Int,
    val offset: Int
)
