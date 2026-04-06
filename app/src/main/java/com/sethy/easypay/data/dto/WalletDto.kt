package com.sethy.easypay.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val currency: String,
    val balanceMinor: Long,
    val balance: Double
)

@Serializable
data class TransactionResponse(
    val id: String,
    val type: String,
    val amountMinor: Long,
    val amount: Double,
    val balanceBeforeMinor: Long,
    val balanceAfterMinor: Long,
    val description: String,
    val transferId: String?,
    val createdAt: String
)

@Serializable
data class TransactionsListResponse(
    val transactions: List<TransactionResponse>,
    val total: Int
)
