package com.sethy.easypay.data.source

import android.content.Context
import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.model.NotificationType
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockWalletDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) : WalletDataSource {

    private var user: User? = null
    private var balance: Double = 0.0
    private var transactions: List<Transaction> = emptyList()
    private var notifications: List<Notification> = emptyList()
    private var loaded = false

    private suspend fun ensureLoaded() {
        if (loaded) return
        val loadedUser = MockDataLoader.loadUser(context)
        user = loadedUser
        balance = loadedUser.balance
        transactions = MockDataLoader.loadTransactions(context)
        notifications = generateNotifications(transactions)
        loaded = true
    }

    override fun setCurrentUser(user: User) {
        ensureLoadedSync()
        this.user = user
    }

    private fun ensureLoadedSync() {
        if (loaded) return
        kotlinx.coroutines.runBlocking {
            if (!loaded) ensureLoaded()
        }
    }

    override suspend fun getUser(): Result<User> {
        ensureLoaded()
        return user?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("User not found"))
    }

    override suspend fun getBalance(): Result<Double> {
        ensureLoaded()
        return Result.success(balance)
    }

    override suspend fun getTransactions(limit: Int, offset: Int): Result<List<Transaction>> {
        ensureLoaded()
        val paginated = transactions.drop(offset).take(limit)
        return Result.success(paginated)
    }

    override suspend fun getTransfer(id: String): Result<Transaction> {
        ensureLoaded()
        val transaction = transactions.find { it.id == id }
        return if (transaction != null) {
            Result.success(transaction)
        } else {
            Result.failure(NoSuchElementException("Transaction not found"))
        }
    }

    override suspend fun createTransfer(
        recipientPhone: String,
        amount: Double,
        idempotencyKey: String,
        note: String?
    ): Result<Transaction> {
        ensureLoaded()
        if (amount > balance) {
            return Result.failure(IllegalStateException("Insufficient balance"))
        }
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            recipientName = recipientPhone,
            amount = amount,
            type = TransactionType.SENT,
            description = "Sent to $recipientPhone",
            status = TransactionStatus.COMPLETED
        )
        balance -= amount
        transactions = listOf(transaction) + transactions
        val notification = Notification(
            id = transaction.id,
            title = "Money sent",
            body = "You sent ${"%.2f".format(amount)} to $recipientPhone",
            type = NotificationType.INFO,
            timestamp = transaction.timestamp,
            isRead = false
        )
        notifications = listOf(notification) + notifications
        return Result.success(transaction)
    }

    override suspend fun payBill(
        billerCode: String,
        accountNumber: String,
        amount: Double,
        note: String?
    ): Result<BillPayment> {
        ensureLoaded()
        if (amount > balance) {
            return Result.failure(IllegalStateException("Insufficient balance"))
        }
        balance -= amount
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            recipientName = note ?: billerCode,
            amount = amount,
            type = TransactionType.SENT,
            description = "Bill payment to $billerCode ($accountNumber)",
            status = TransactionStatus.COMPLETED
        )
        transactions = listOf(transaction) + transactions
        return Result.success(
            BillPayment(
                transactionId = transaction.id,
                walletId = "mock-wallet",
                balanceAfterMinor = (balance * 100).toLong(),
                amountMinor = (amount * 100).toLong()
            )
        )
    }

    override suspend fun topUp(amount: Double, note: String?): Result<TopUp> {
        ensureLoaded()
        balance += amount
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            recipientName = "Top up",
            amount = amount,
            type = TransactionType.RECEIVED,
            description = note ?: "Top up",
            status = TransactionStatus.COMPLETED
        )
        transactions = listOf(transaction) + transactions
        return Result.success(
            TopUp(
                transactionId = transaction.id,
                walletId = "mock-wallet",
                balanceAfterMinor = (balance * 100).toLong(),
                amountMinor = (amount * 100).toLong()
            )
        )
    }

    override suspend fun getNotifications(): Result<List<Notification>> {
        ensureLoaded()
        return Result.success(notifications)
    }

    override suspend fun markNotificationRead(id: String): Result<Unit> {
        ensureLoaded()
        notifications = notifications.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        return Result.success(Unit)
    }

    private fun generateNotifications(transactions: List<Transaction>): List<Notification> {
        return transactions.map { tx ->
            val title = if (tx.type == TransactionType.RECEIVED) "Payment received" else "Money sent"
            val body = when (tx.type) {
                TransactionType.RECEIVED -> "You received ${"%.2f".format(tx.amount)} from ${tx.recipientName}"
                TransactionType.SENT -> "You sent ${"%.2f".format(tx.amount)} to ${tx.recipientName}"
            }
            Notification(
                id = tx.id,
                title = title,
                body = body,
                type = if (tx.type == TransactionType.RECEIVED) NotificationType.RECEIPT else NotificationType.INFO,
                timestamp = tx.timestamp,
                isRead = false
            )
        }
    }
}
