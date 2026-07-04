package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.source.BillPayment
import com.sethy.easypay.data.repository.WalletRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PayBillUseCaseTest {

    private fun useCase(repo: WalletRepository = mock()) = PayBillUseCase(repo)

    @Test
    fun invoke_converts_major_to_minor_and_calls_repository() = runTest {
        val repo: WalletRepository = mock()
        val payment = BillPayment(
            transactionId = "tx-1",
            walletId = "w-1",
            balanceAfterMinor = 12750,
            amountMinor = 2499
        )
        whenever(
            repo.payBill(
                billerCode = "glitch",
                accountNumber = "game-1",
                amountMinor = 2499L,
                note = "Hades II"
            )
        ).thenReturn(Result.success(payment))

        val result = useCase(repo).invoke(
            billerCode = "glitch",
            accountNumber = "game-1",
            amountMajor = 24.99,
            note = "Hades II"
        )

        assertTrue(result.isSuccess)
        assertEquals(payment, result.getOrNull())
        verify(repo).payBill("glitch", "game-1", 2499L, "Hades II")
    }

    @Test
    fun invoke_propagates_insufficient_funds_failure() = runTest {
        val repo: WalletRepository = mock()
        whenever(
            repo.payBill(
                billerCode = "glitch",
                accountNumber = "game-1",
                amountMinor = 5000L,
                note = null
            )
        ).thenReturn(Result.failure(IllegalStateException("Insufficient balance")))

        val result = useCase(repo).invoke(
            billerCode = "glitch",
            accountNumber = "game-1",
            amountMajor = 50.0,
            note = null
        )

        assertTrue(result.isFailure)
        assertEquals(
            "Insufficient balance",
            result.exceptionOrNull()?.message
        )
    }
}