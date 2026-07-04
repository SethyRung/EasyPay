package com.sethy.easypay.data.mapper

import com.sethy.easypay.data.dto.BalanceResponse
import com.sethy.easypay.data.dto.BillPaymentResponse
import com.sethy.easypay.data.dto.NotificationResponse
import com.sethy.easypay.data.dto.TopUpResponse
import com.sethy.easypay.data.dto.TransactionResponse
import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.model.NotificationType
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.source.BillPayment
import com.sethy.easypay.data.source.TopUp
import java.time.Instant
import java.time.format.DateTimeParseException

fun BalanceResponse.toBalance(): Double = balance

fun TransactionResponse.toTransaction(): Transaction = Transaction(
    id = id,
    recipientName = description,
    amount = amount,
    type = if (type == "credit") TransactionType.RECEIVED else TransactionType.SENT,
    timestamp = parseDateToMillis(createdAt),
    avatarUrl = null,
    description = description,
    status = TransactionStatus.COMPLETED
)

fun NotificationResponse.toNotification(): Notification = Notification(
    id = id,
    title = title,
    body = body,
    type = runCatching { NotificationType.valueOf(type) }.getOrDefault(NotificationType.INFO),
    timestamp = timestamp,
    isRead = isRead
)

fun BillPaymentResponse.toBillPayment(): BillPayment = BillPayment(
    transactionId = transactionId,
    walletId = walletId,
    balanceAfterMinor = balanceAfterMinor,
    amountMinor = amountMinor
)

fun TopUpResponse.toTopUp(): TopUp = TopUp(
    transactionId = transactionId,
    walletId = walletId,
    balanceAfterMinor = balanceAfterMinor,
    amountMinor = amountMinor
)

private fun parseDateToMillis(dateString: String): Long {
    return try {
        Instant.parse(dateString).toEpochMilli()
    } catch (_: DateTimeParseException) {
        System.currentTimeMillis()
    }
}
