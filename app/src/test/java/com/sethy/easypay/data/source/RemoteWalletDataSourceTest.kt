package com.sethy.easypay.data.source

import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.api.NotificationApi
import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.BillPaymentResponse
import com.sethy.easypay.data.dto.CreateTransferDto
import com.sethy.easypay.data.dto.SessionDto
import com.sethy.easypay.data.dto.SessionResponse
import com.sethy.easypay.data.dto.Status
import com.sethy.easypay.data.dto.TopUpResponse
import com.sethy.easypay.data.dto.TransactionResponse
import com.sethy.easypay.data.dto.TransactionsListResponse
import com.sethy.easypay.data.dto.TransferReceiptDto
import com.sethy.easypay.data.dto.UserDto
import com.sethy.easypay.data.repository.BaseRepository
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RemoteWalletDataSourceTest {

    private val walletApi: WalletApi = mock()
    private val notificationApi: NotificationApi = mock()
    private val authApi: AuthApi = mock()
    private val notifier = AuthSessionNotifier()

    private fun createDataSource() = RemoteWalletDataSource(walletApi, notificationApi, authApi, notifier)

    private fun okEnvelope(data: Any?): ApiResponse<Any?> = ApiResponse(
        status = Status(code = "SUCCESS", message = "ok", requestId = "req-1", requestTime = 0L),
        data = data
    )

    // ─── getUser ─────────────────────────────────────────────────────────────

    @Test
    fun `getUser returns user from getSession`() = runTest {
        val userDto = UserDto(
            id = "u-1",
            email = "alice@example.com",
            name = "Alice Smith",
            phone = "+1234567890"
        )
        val session = SessionDto(
            id = "s-1",
            userId = "u-1",
            token = "tok",
            expiresAt = "2099-01-01T00:00:00Z"
        )
        whenever(authApi.getSession()).thenReturn(
            ApiResponse<SessionResponse>(
                status = Status(code = "SUCCESS", message = "ok", requestId = "r", requestTime = 0L),
                data = SessionResponse(user = userDto, session = session)
            )
        )

        val result = createDataSource().getUser()

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("u-1", user!!.id)
        assertEquals("Alice Smith", user.name)
        assertEquals("alice@example.com", user.email)
    }

    // ─── createTransfer ─────────────────────────────────────────────────────

    @Test
    fun `createTransfer POSTs to api_transfers with all required fields`() = runTest {
        val receipt = TransferReceiptDto(
            id = "tr-1",
            senderUserId = "u-1",
            recipientUserId = "u-2",
            amountMinor = 5000,
            amount = 50.0,
            feeMinor = 0,
            fee = 0.0,
            totalDebitMinor = 5000,
            totalDebit = 50.0,
            status = "completed",
            idempotencyKey = "any-uuid",
            note = "lunch",
            createdAt = "2026-07-16T10:00:00Z",
            senderBalanceBeforeMinor = 100000,
            senderBalanceAfterMinor = 95000,
            recipientBalanceBeforeMinor = 0,
            recipientBalanceAfterMinor = 5000
        )
        whenever(walletApi.createTransfer(any())).thenReturn(
            ApiResponse(
                status = Status(code = "SUCCESS", message = "ok", requestId = "r", requestTime = 0L),
                data = receipt
            )
        )

        val result = createDataSource().createTransfer(
            recipientPhone = "+1234567890",
            amount = 50.0,
            idempotencyKey = "any-uuid",
            note = "lunch"
        )

        val captor = argumentCaptor<CreateTransferDto>()
        verify(walletApi).createTransfer(captor.capture())
        val sent = captor.firstValue
        assertEquals("+1234567890", sent.recipientPhone)
        assertEquals(50.0, sent.amount, 0.0)
        assertEquals("any-uuid", sent.idempotencyKey)
        assertEquals("lunch", sent.note)

        assertTrue(result.isSuccess)
        assertEquals("tr-1", result.getOrNull()!!.id)
    }

    // ─── getTransfer ─────────────────────────────────────────────────────────

    @Test
    fun `getTransfer hits api_transfers slash id and maps response`() = runTest {
        val receipt = TransferReceiptDto(
            id = "tr-9",
            senderUserId = "u-1",
            recipientUserId = "u-2",
            amountMinor = 2500,
            amount = 25.0,
            feeMinor = 0,
            fee = 0.0,
            totalDebitMinor = 2500,
            totalDebit = 25.0,
            status = "completed",
            idempotencyKey = "uuid",
            note = null,
            createdAt = "2026-07-16T10:00:00Z",
            senderBalanceBeforeMinor = 100000,
            senderBalanceAfterMinor = 97500,
            recipientBalanceBeforeMinor = 0,
            recipientBalanceAfterMinor = 2500
        )
        whenever(walletApi.getTransfer("tr-9")).thenReturn(
            ApiResponse(
                status = Status(code = "SUCCESS", message = "ok", requestId = "r", requestTime = 0L),
                data = receipt
            )
        )

        val result = createDataSource().getTransfer("tr-9")

        assertTrue(result.isSuccess)
        assertEquals("tr-9", result.getOrNull()!!.id)
        assertEquals(25.0, result.getOrNull()!!.amount, 0.0)
    }

    // ─── payBill ────────────────────────────────────────────────────────────

    @Test
    fun `payBill sends amount as Double in request body`() = runTest {
        whenever(walletApi.payBill(any())).thenReturn(
            ApiResponse(
                status = Status(code = "SUCCESS", message = "ok", requestId = "r", requestTime = 0L),
                data = BillPaymentResponse(
                    transactionId = "tx-1",
                    walletId = "w-1",
                    balanceAfterMinor = 7500,
                    amountMinor = 2500
                )
            )
        )

        val result = createDataSource().payBill(
            billerCode = "glitch",
            accountNumber = "game-1",
            amount = 25.0,
            note = "Hades II"
        )

        val captor = argumentCaptor<com.sethy.easypay.data.dto.BillPaymentRequest>()
        verify(walletApi).payBill(captor.capture())
        assertEquals(25.0, captor.firstValue.amount, 0.0)

        assertTrue(result.isSuccess)
        assertEquals("tx-1", result.getOrNull()!!.transactionId)
    }

    // ─── topUp ───────────────────────────────────────────────────────────────

    @Test
    fun `topUp sends amount as Double in request body`() = runTest {
        whenever(walletApi.topUp(any())).thenReturn(
            ApiResponse(
                status = Status(code = "SUCCESS", message = "ok", requestId = "r", requestTime = 0L),
                data = TopUpResponse(
                    transactionId = "tx-2",
                    walletId = "w-1",
                    balanceAfterMinor = 12500,
                    amountMinor = 2500
                )
            )
        )

        val result = createDataSource().topUp(25.0, "Bonus")

        val captor = argumentCaptor<com.sethy.easypay.data.dto.TopUpRequest>()
        verify(walletApi).topUp(captor.capture())
        assertEquals(25.0, captor.firstValue.amount, 0.0)
        assertEquals("Bonus", captor.firstValue.note)

        assertTrue(result.isSuccess)
    }

    // ─── getTransactions ───────────────────────────────────────────────────

    @Test
    fun `getTransactions maps list response`() = runTest {
        val txResp = TransactionResponse(
            id = "tx-1",
            type = "debit",
            amountMinor = 5000,
            amount = 50.0,
            balanceBeforeMinor = 100000,
            balanceAfterMinor = 95000,
            description = "Sent",
            transferId = null,
            createdAt = "2026-07-16T10:00:00Z"
        )
        whenever(walletApi.getTransactions(10, 0)).thenReturn(
            ApiResponse(
                status = Status(code = "SUCCESS", message = "ok", requestId = "r", requestTime = 0L),
                data = TransactionsListResponse(transactions = listOf(txResp), total = 1)
            )
        )

        val result = createDataSource().getTransactions(10, 0)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("tx-1", result.getOrNull()!!.first().id)
    }

    // ─── error mapping ──────────────────────────────────────────────────────

    @Test
    fun `payBill returns failure with UnexpectedException on HttpException`() = runTest {
        val errorBody = "Bad request".toResponseBody("application/json".toMediaType())
        whenever(walletApi.payBill(any())).thenThrow(
            HttpException(Response.error<Any>(400, errorBody))
        )

        val result = createDataSource().payBill("b", "a", 10.0, null)

        assertTrue("expected failure, got $result", result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue(
            "expected UnexpectedException, got ${ex!!::class.simpleName}",
            ex is BaseRepository.UnexpectedException
        )
    }

    @Test
    fun `payBill returns failure with NetworkException on IOException`() = runTest {
        whenever(walletApi.payBill(any())).thenAnswer { throw IOException("connection refused") }

        val result = createDataSource().payBill("b", "a", 10.0, null)

        assertTrue("expected failure, got $result", result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue("expected NetworkException, got ${ex!!::class.simpleName}", ex is BaseRepository.NetworkException)
    }
}
