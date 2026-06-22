package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.design.components.BottomNavItem
import com.sethy.easypay.domain.usecase.GetBalanceUseCase
import com.sethy.easypay.domain.usecase.GetTransactionsUseCase
import com.sethy.easypay.ui.state.HomeEffect
import com.sethy.easypay.ui.state.HomeEvent
import com.sethy.easypay.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBalance: GetBalanceUseCase,
    private val getTransactions: GetTransactionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect: Flow<HomeEffect> = _effect.receiveAsFlow()

    init {
        load()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> load()
            HomeEvent.SendMoneyClick -> viewModelScope.launch {
                _effect.send(HomeEffect.NavigateToSendMoney)
            }
            is HomeEvent.TransactionClick -> viewModelScope.launch {
                _effect.send(HomeEffect.NavigateToTransactionDetail(event.id))
            }
            is HomeEvent.BottomNavSelected -> handleBottomNav(event.item)
            HomeEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun handleBottomNav(item: BottomNavItem) {
        viewModelScope.launch {
            val effect = when (item) {
                BottomNavItem.Home -> return@launch
                BottomNavItem.Calendar -> HomeEffect.NavigateToCalendar
                BottomNavItem.Notifications -> HomeEffect.NavigateToNotifications
                BottomNavItem.Profile -> HomeEffect.NavigateToProfile
            }
            _effect.send(effect)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val balanceDeferred = async { getBalance() }
            val transactionsDeferred = async { getTransactions() }

            val balanceResult = balanceDeferred.await()
            val transactionsResult = transactionsDeferred.await()

            val balance = balanceResult.getOrNull() ?: 0.0
            val transactions = transactionsResult.getOrNull() ?: emptyList()

            val error = balanceResult.exceptionOrNull()?.message
                ?: transactionsResult.exceptionOrNull()?.message

            _state.value = _state.value.copy(
                isLoading = false,
                balance = balance,
                transactions = transactions,
                errorMessage = error
            )
        }
    }
}
