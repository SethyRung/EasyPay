package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.data.repository.WalletRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SendMoneyUseCaseTest {

    private val walletRepository: WalletRepository = mock()
    private val sendMoneyUseCase = SendMoneyUseCase(walletRepository)

    private val testTransaction = Transaction(
        id = "tx-1",
        recipientName = "Charlie Davis",
        amount = 50.0,
        type = TransactionType.SENT,
        status = TransactionStatus.COMPLETED
    )

    @Test
    fun `invoke returns success when transfer succeeds`() = runTest {
        whenever(walletRepository.sendMoney("Charlie Davis", 50.0))
            .thenReturn(Result.success(testTransaction))

        val result = sendMoneyUseCase("Charlie Davis", 50.0)

        assertTrue(result.isSuccess)
        assertEquals(testTransaction, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when insufficient balance`() = runTest {
        val exception = Exception("Insufficient balance")
        whenever(walletRepository.sendMoney("Charlie Davis", 10_000.0))
            .thenReturn(Result.failure(exception))

        val result = sendMoneyUseCase("Charlie Davis", 10_000.0)

        assertTrue(result.isFailure)
        assertEquals("Insufficient balance", result.exceptionOrNull()?.message)
    }
}
