package com.sethy.easypay.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BridgeIssueResponse(
    val cookie: String? = null
)