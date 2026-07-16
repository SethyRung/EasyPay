package com.sethy.easypay.data.repository

import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.BillPayment
import com.sethy.easypay.data.source.TopUp
import com.sethy.easypay.data.source.WalletDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
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
        phone = "+1234567890",
        balance = 1_000.0
    )

    private val testTransaction = Transaction(
        id = "tx-1",
        recipientName = "Charlie Davis",
        amount = 50.0,
        type = TransactionType.SENT,
        status = TransactionStatus.COMPLETED
    )

    private fun createRepository() = DefaultWalletRepository(
        walletDataSource,
        AuthSessionNotifier()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── getCurrentUser ─────────────────────────────────────────────────────

    @Test
    fun `getCurrentUser delegates to data source`() = runTest {
        whenever(walletDataSource.getUser()).thenReturn(Result.success(testUser))

        val repo = createRepository()
        val result = repo.getCurrentUser()

        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
        verify(walletDataSource).getUser()
    }

    // ─── getBalance ─────────────────────────────────────────────────────────

    @Test
    fun `getBalance delegates to data source`() = runTest {
        whenever(walletDataSource.getBalance()).thenReturn(Result.success(123.45))

        val repo = createRepository()
        val result = repo.getBalance()

        assertEquals(123.45, result.getOrNull()!!, 0.0)
    }

    // ─── getTransactions ───────────────────────────────────────────────────

    @Test
    fun `getTransactions delegates with limit and offset`() = runTest {
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
    fun `getTransactions uses default limit and offset`() = runTest {
        whenever(walletDataSource.getTransactions(10, 0))
            .thenReturn(Result.success(emptyList()))

        val repo = createRepository()
        repo.getTransactions()

        verify(walletDataSource).getTransactions(10, 0)
    }

    // ─── getTransfer ─────────────────────────────────────────────────────────

    @Test
    fun `getTransfer delegates to data source`() = runTest {
        whenever(walletDataSource.getTransfer("tx-1"))
            .thenReturn(Result.success(testTransaction))

        val repo = createRepository()
        val result = repo.getTransfer("tx-1")

        assertTrue(result.isSuccess)
        assertEquals(testTransaction, result.getOrNull())
    }

    // ─── createTransfer ─────────────────────────────────────────────────────

    @Test
    fun `createTransfer generates idempotencyKey and forwards to data source`() = runTest {
        whenever(walletDataSource.createTransfer(any(), any(), any(), any<String>()))
            .thenReturn(Result.success(testTransaction))

        val repo = createRepository()
        val result = repo.createTransfer("+1234567890", 50.0, "lunch")

        assertTrue(result.isSuccess)
        val captor = argumentCaptor<String>()
        verify(walletDataSource).createTransfer(
            org.mockito.kotlin.eq("+1234567890"),
            org.mockito.kotlin.eq(50.0),
            captor.capture(),
            org.mockito.kotlin.eq("lunch")
        )
        // Verify the captured idempotencyKey is a valid UUID
        val key = captor.firstValue
        assertTrue("idempotencyKey should be a non-blank UUID, got '$key'", key.isNotBlank())
        try {
            java.util.UUID.fromString(key)
        } catch (e: IllegalArgumentException) {
            org.junit.Assert.fail("idempotencyKey is not a valid UUID: $key")
        }
    }

    @Test
    fun `createTransfer propagates failure`() = runTest {
        whenever(walletDataSource.createTransfer(any(), any(), any(), org.mockito.kotlin.isNull()))
            .thenReturn(Result.failure(Exception("Insufficient balance")))

        val repo = createRepository()
        val result = repo.createTransfer("+1", 50.0, null)

        assertTrue(result.isFailure)
        assertEquals("Insufficient balance", result.exceptionOrNull()?.message)
    }

    // ─── payBill ────────────────────────────────────────────────────────────

    @Test
    fun `payBill forwards major-unit amount directly to data source`() = runTest {
        val payment = BillPayment(
            transactionId = "tx-1",
            walletId = "w-1",
            balanceAfterMinor = 12750,
            amountMinor = 2499
        )
        whenever(walletDataSource.payBill("glitch", "game-1", 24.99, "Hades II"))
            .thenReturn(Result.success(payment))

        val repo = createRepository()
        val result = repo.payBill("glitch", "game-1", 24.99, "Hades II")

        assertTrue(result.isSuccess)
        assertEquals(payment, result.getOrNull())
        verify(walletDataSource).payBill("glitch", "game-1", 24.99, "Hades II")
    }

    @Test
    fun `payBill propagates insufficient funds failure`() = runTest {
        whenever(walletDataSource.payBill("glitch", "game-1", 50.0, null))
            .thenReturn(Result.failure(IllegalStateException("Insufficient balance")))

        val repo = createRepository()
        val result = repo.payBill("glitch", "game-1", 50.0, null)

        assertTrue(result.isFailure)
        assertEquals("Insufficient balance", result.exceptionOrNull()?.message)
    }

    // ─── topUp ───────────────────────────────────────────────────────────────

    @Test
    fun `topUp forwards major-unit amount directly to data source`() = runTest {
        val topUp = TopUp("tx-2", "w-1", 12500, 2500)
        whenever(walletDataSource.topUp(25.0, "Bonus"))
            .thenReturn(Result.success(topUp))

        val repo = createRepository()
        val result = repo.topUp(25.0, "Bonus")

        assertTrue(result.isSuccess)
        verify(walletDataSource).topUp(25.0, "Bonus")
    }
}
