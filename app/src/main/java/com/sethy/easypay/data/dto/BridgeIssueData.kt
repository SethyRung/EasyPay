package com.sethy.easypay.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BridgeIssueData(
    val ticket: String
)
