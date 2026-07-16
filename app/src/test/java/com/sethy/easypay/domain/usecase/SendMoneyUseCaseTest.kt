package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.repository.WalletRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SendMoneyUseCaseTest {

    private val walletRepository: WalletRepository = mock()
    private val sendMoneyUseCase = SendMoneyUseCase(walletRepository)

    private val testTransaction = Transaction(
        id = "tx-1",
        recipientName = "+1234567890",
        amount = 50.0,
        type = TransactionType.SENT,
        status = TransactionStatus.COMPLETED
    )

    @Test
    fun `invoke returns success when transfer succeeds`() = runTest {
        whenever(walletRepository.createTransfer("+1234567890", 50.0, null))
            .thenReturn(Result.success(testTransaction))

        val result = sendMoneyUseCase("+1234567890", 50.0)

        assertTrue(result.isSuccess)
        assertEquals(testTransaction, result.getOrNull())
        verify(walletRepository).createTransfer("+1234567890", 50.0, null)
    }

    @Test
    fun `invoke returns failure when insufficient balance`() = runTest {
        val exception = Exception("Insufficient balance")
        whenever(walletRepository.createTransfer("+1234567890", 10_000.0, null))
            .thenReturn(Result.failure(exception))

        val result = sendMoneyUseCase("+1234567890", 10_000.0)

        assertTrue(result.isFailure)
        assertEquals("Insufficient balance", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke passes note through to repository`() = runTest {
        whenever(walletRepository.createTransfer("+1234567890", 25.0, "dinner"))
            .thenReturn(Result.success(testTransaction))

        val result = sendMoneyUseCase("+1234567890", 25.0, "dinner")

        assertTrue(result.isSuccess)
        verify(walletRepository).createTransfer("+1234567890", 25.0, "dinner")
    }
}
