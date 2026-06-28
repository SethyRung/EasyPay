package com.sethy.easypay.ui.viewmodel

import app.cash.turbine.test
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionStatus
import com.sethy.easypay.data.model.TransactionType
import com.sethy.easypay.design.components.BottomNavItem
import com.sethy.easypay.domain.usecase.GetBalanceUseCase
import com.sethy.easypay.domain.usecase.GetTransactionsUseCase
import com.sethy.easypay.ui.state.HomeEffect
import com.sethy.easypay.ui.state.HomeEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class HomeViewModelTest {

    private fun TestScope.createViewModel(): HomeViewModel {
        val getBalance: GetBalanceUseCase = mock()
        val getTransactions: GetTransactionsUseCase = mock()
        return HomeViewModel(getBalance, getTransactions)
    }

    private val testTransactions = listOf(
        Transaction(
            id = "tx-1",
            recipientName = "Charlie Davis",
            amount = 50.0,
            type = TransactionType.SENT,
            status = TransactionStatus.COMPLETED
        ),
        Transaction(
            id = "tx-2",
            recipientName = "Dana Lee",
            amount = 25.0,
            type = TransactionType.RECEIVED,
            status = TransactionStatus.COMPLETED
        )
    )

    @Test
    fun initial_load_sets_isLoading_true_then_false() = runTest {
        val vm = createViewModel()

        assertTrue(vm.state.value.isLoading)

        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun load_populates_balance_and_transactions() = runTest {
        val getBalance: GetBalanceUseCase = mock()
        val getTransactions: GetTransactionsUseCase = mock()
        whenever(getBalance()).thenReturn(Result.success(1_250.75))
        whenever(getTransactions()).thenReturn(Result.success(testTransactions))

        val vm = HomeViewModel(getBalance, getTransactions)
        advanceUntilIdle()

        assertEquals(null, 1_250.75, vm.state.value.balance, 0.0)
        assertEquals(null, testTransactions, vm.state.value.transactions)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun load_sets_errorMessage_on_failure() = runTest {
        val getBalance: GetBalanceUseCase = mock()
        val getTransactions: GetTransactionsUseCase = mock()
        whenever(getBalance()).thenReturn(Result.failure(Exception("Network error")))
        whenever(getTransactions()).thenReturn(Result.success(testTransactions))

        val vm = HomeViewModel(getBalance, getTransactions)
        advanceUntilIdle()

        assertEquals(null, "Network error", vm.state.value.errorMessage)
        assertEquals(null, testTransactions, vm.state.value.transactions)
    }

    @Test
    fun Refresh_reloads_data() = runTest {
        val getBalance: GetBalanceUseCase = mock()
        val getTransactions: GetTransactionsUseCase = mock()
        whenever(getBalance())
            .thenReturn(Result.success(1_250.75))
            .thenReturn(Result.success(2_000.0))
        whenever(getTransactions())
            .thenReturn(Result.success(testTransactions))
            .thenReturn(Result.success(emptyList()))

        val vm = HomeViewModel(getBalance, getTransactions)
        advanceUntilIdle()
        assertEquals(null, 1_250.75, vm.state.value.balance, 0.0)

        vm.onEvent(HomeEvent.Refresh)
        advanceUntilIdle()
        assertEquals(null, 2_000.0, vm.state.value.balance, 0.0)
    }

    @Test
    fun SendMoneyClick_emits_NavigateToSendMoney() = runTest {
        val getBalance: GetBalanceUseCase = mock()
        val getTransactions: GetTransactionsUseCase = mock()
        whenever(getBalance()).thenReturn(Result.success(0.0))
        whenever(getTransactions()).thenReturn(Result.success(emptyList()))

        val vm = HomeViewModel(getBalance, getTransactions)
        advanceUntilIdle()

        vm.effect.test {
            vm.onEvent(HomeEvent.SendMoneyClick)
            advanceUntilIdle()
            assertEquals(HomeEffect.NavigateToSendMoney, awaitItem())
        }
    }

    @Test
    fun TransactionClick_emits_NavigateToTransactionDetail() = runTest {
        val getBalance: GetBalanceUseCase = mock()
        val getTransactions: GetTransactionsUseCase = mock()
        whenever(getBalance()).thenReturn(Result.success(0.0))
        whenever(getTransactions()).thenReturn(Result.success(testTransactions))

        val vm = HomeViewModel(getBalance, getTransactions)
        advanceUntilIdle()

        vm.effect.test {
            vm.onEvent(HomeEvent.TransactionClick("tx-1"))
            advanceUntilIdle()
            assertEquals(HomeEffect.NavigateToTransactionDetail("tx-1"), awaitItem())
        }
    }

    @Test
    fun DismissError_clears_errorMessage() = runTest {
        val getBalance: GetBalanceUseCase = mock()
        val getTransactions: GetTransactionsUseCase = mock()
        whenever(getBalance()).thenReturn(Result.failure(Exception("Network error")))
        whenever(getTransactions()).thenReturn(Result.success(emptyList()))

        val vm = HomeViewModel(getBalance, getTransactions)
        advanceUntilIdle()
        assertEquals(null, "Network error", vm.state.value.errorMessage)

        vm.onEvent(HomeEvent.DismissError)
        advanceUntilIdle()
        assertNull(vm.state.value.errorMessage)
    }
}
