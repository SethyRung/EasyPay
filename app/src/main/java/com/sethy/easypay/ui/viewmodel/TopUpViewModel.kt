package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.domain.usecase.GetBalanceUseCase
import com.sethy.easypay.domain.usecase.TopUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TopUpUiState(
    val isLoading: Boolean = false,
    val balance: Double = 0.0,
    val amountMajor: Double? = null,
    val errorMessage: String? = null
)

sealed interface TopUpEvent {
    data class AmountSelected(val amountMajor: Double) : TopUpEvent
    data object Confirm : TopUpEvent
    data object DismissError : TopUpEvent
}

sealed interface TopUpEffect {
    data object Done : TopUpEffect
}

@HiltViewModel
class TopUpViewModel @Inject constructor(
    private val getBalance: GetBalanceUseCase,
    private val topUpUseCase: TopUpUseCase
) : ViewModel() {

    val presetAmounts = listOf(10.0, 25.0, 50.0, 100.0)

    private val _state = MutableStateFlow(TopUpUiState())
    val state: StateFlow<TopUpUiState> = _state.asStateFlow()

    private val _effect = Channel<TopUpEffect>(Channel.BUFFERED)
    val effect: Flow<TopUpEffect> = _effect.receiveAsFlow()

    init {
        loadBalance()
    }

    fun onEvent(event: TopUpEvent) {
        when (event) {
            is TopUpEvent.AmountSelected -> {
                _state.value = _state.value.copy(amountMajor = event.amountMajor, errorMessage = null)
            }
            TopUpEvent.Confirm -> confirm()
            TopUpEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun confirm() {
        val amount = _state.value.amountMajor ?: return
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            topUpUseCase(amountMajor = amount)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        balance = it.balanceAfterMinor / 100.0,
                        amountMajor = null
                    )
                    _effect.send(TopUpEffect.Done)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Top up failed"
                    )
                }
        }
    }

    private fun loadBalance() {
        viewModelScope.launch {
            getBalance().onSuccess { balance ->
                _state.value = _state.value.copy(balance = balance)
            }
        }
    }
}