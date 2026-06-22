package com.sethy.easypay.data.repository

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.WalletDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultWalletRepositoryTest {

    private val walletDataSource: WalletDataSource = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val testUser = User(
        id = "user-1",
        name = "Alice Smith",
        email = "alice@example.com",
        balance = 1_000.0
    )

    private val testTransaction = Transaction(
        id = "tx-1",
        recipientName = "Charlie Davis",
        amount = 50.0,
        type = TransactionType.SENT,
        status = TransactionStatus.COMPLETED
    )

    private fun createRepository() = DefaultWalletRepository(walletDataSource)

    // ─── getCurrentUser ─────────────────────────────────────────────────────

    @Test
    fun `getCurrentUser returns user on success`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.getUser()).thenReturn(Result.success(testUser))

        val repo = createRepository()
        val result = repo.getCurrentUser()

        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
        verify(walletDataSource).getUser()
    }

    @Test
    fun `getCurrentUser returns failure on error`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.getUser())
            .thenReturn(Result.failure(Exception("Not found")))

        val repo = createRepository()
        val result = repo.getCurrentUser()

        assertTrue(result.isFailure)
        assertEquals("Not found", result.exceptionOrNull()?.message)
    }

    // ─── getBalance ─────────────────────────────────────────────────────────

    @Test
    fun `getBalance returns balance on success`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.getBalance()).thenReturn(Result.success(1_250.75))

        val repo = createRepository()
        val result = repo.getBalance()

        assertTrue(result.isSuccess)
        assertEquals(1_250.75, result.getOrNull() ?: 0.0, 0.0)
    }

    @Test
    fun `getBalance returns failure on error`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.getBalance())
            .thenReturn(Result.failure(Exception("Network error")))

        val repo = createRepository()
        val result = repo.getBalance()

        assertTrue(result.isFailure)
    }

    // ─── getTransactions ─────────────────────────────────────────────────────

    @Test
    fun `getTransactions delegates with limit and offset`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        val transactions = listOf(testTransaction)
        whenever(walletDataSource.getTransactions(10, 0))
            .thenReturn(Result.success(transactions))

        val repo = createRepository()
        val result = repo.getTransactions(10, 0)

        assertTrue(result.isSuccess)
        assertEquals(transactions, result.getOrNull())
        verify(walletDataSource).getTransactions(10, 0)
    }

    @Test
    fun `getTransactions uses default limit and offset`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.getTransactions(10, 0))
            .thenReturn(Result.success(emptyList()))

        val repo = createRepository()
        repo.getTransactions()

        verify(walletDataSource).getTransactions(10, 0)
    }

    // ─── getTransaction ─────────────────────────────────────────────────────

    @Test
    fun `getTransaction returns transaction on success`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.getTransaction("tx-1"))
            .thenReturn(Result.success(testTransaction))

        val repo = createRepository()
        val result = repo.getTransaction("tx-1")

        assertTrue(result.isSuccess)
        assertEquals(testTransaction, result.getOrNull())
    }

    // ─── sendMoney ───────────────────────────────────────────────────────────

    @Test
    fun `sendMoney converts amount to minor units`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.sendMoney("Charlie Davis", 5000L))
            .thenReturn(Result.success(testTransaction))

        val repo = createRepository()
        val result = repo.sendMoney("Charlie Davis", 50.0)

        assertTrue(result.isSuccess)
        verify(walletDataSource).sendMoney("Charlie Davis", 5000L)
    }

    @Test
    fun `sendMoney handles decimal conversion correctly`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.sendMoney("Charlie Davis", 999L))
            .thenReturn(Result.success(testTransaction))

        val repo = createRepository()
        repo.sendMoney("Charlie Davis", 9.99)

        verify(walletDataSource).sendMoney("Charlie Davis", 999L)
    }

    @Test
    fun `sendMoney returns failure on error`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(walletDataSource.sendMoney("Charlie Davis", 5000L))
            .thenReturn(Result.failure(Exception("Insufficient balance")))

        val repo = createRepository()
        val result = repo.sendMoney("Charlie Davis", 50.0)

        assertTrue(result.isFailure)
        assertEquals("Insufficient balance", result.exceptionOrNull()?.message)
    }
}
