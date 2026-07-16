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
    fun invoke_forwards_amount_in_major_units_to_repository() = runTest {
        val repo: WalletRepository = mock()
        val payment = BillPayment(
            transactionId = "tx-1",
            walletId = "w-1",
            balanceAfterMinor = 12750,
            amountMinor = 2499
        )
        whenever(repo.payBill("glitch", "game-1", 24.99, "Hades II"))
            .thenReturn(Result.success(payment))

        val result = useCase(repo).invoke(
            billerCode = "glitch",
            accountNumber = "game-1",
            amountMajor = 24.99,
            note = "Hades II"
        )

        assertTrue(result.isSuccess)
        assertEquals(payment, result.getOrNull())
        verify(repo).payBill("glitch", "game-1", 24.99, "Hades II")
    }

    @Test
    fun invoke_propagates_insufficient_funds_failure() = runTest {
        val repo: WalletRepository = mock()
        whenever(repo.payBill("glitch", "game-1", 50.0, null))
            .thenReturn(Result.failure(IllegalStateException("Insufficient balance")))

        val result = useCase(repo).invoke(
            billerCode = "glitch",
            accountNumber = "game-1",
            amountMajor = 50.0,
            note = null
        )

        assertTrue(result.isFailure)
        assertEquals("Insufficient balance", result.exceptionOrNull()?.message)
    }

    @Test
    fun invoke_rejects_non_positive_amount() = runTest {
        val repo: WalletRepository = mock()
        try {
            useCase(repo).invoke(
                billerCode = "b",
                accountNumber = "a",
                amountMajor = -1.0,
                note = null
            )
            org.junit.Assert.fail("Expected IllegalArgumentException for negative amount")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("positive") == true)
        }
    }
}
