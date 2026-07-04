package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.source.TopUp
import com.sethy.easypay.data.repository.WalletRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TopUpUseCaseTest {

    private fun useCase(repo: WalletRepository = mock()) = TopUpUseCase(repo)

    @Test
    fun invoke_converts_major_to_minor_and_calls_repository() = runTest {
        val repo: WalletRepository = mock()
        val topUp = TopUp(
            transactionId = "tx-2",
            walletId = "w-1",
            balanceAfterMinor = 15000,
            amountMinor = 2500
        )
        whenever(
            repo.topUp(
                amountMinor = 2500L,
                note = null
            )
        ).thenReturn(Result.success(topUp))

        val result = useCase(repo).invoke(amountMajor = 25.0)

        assertTrue(result.isSuccess)
        assertEquals(topUp, result.getOrNull())
        verify(repo).topUp(2500L, null)
    }

    @Test
    fun invoke_passes_through_note() = runTest {
        val repo: WalletRepository = mock()
        whenever(repo.topUp(amountMinor = 10000L, note = "Bonus"))
            .thenReturn(Result.success(TopUp("tx-3", "w-1", 25000, 10000)))

        val result = useCase(repo).invoke(amountMajor = 100.0, note = "Bonus")

        assertTrue(result.isSuccess)
        verify(repo).topUp(10000L, "Bonus")
    }
}