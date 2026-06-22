package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.domain.usecase.SendMoneyUseCase
import com.sethy.easypay.ui.state.SendMoneyEffect
import com.sethy.easypay.ui.state.SendMoneyEvent
import com.sethy.easypay.ui.state.SendMoneyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    private val sendMoneyUseCase: SendMoneyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SendMoneyUiState())
    val state: StateFlow<SendMoneyUiState> = _state.asStateFlow()

    private val _effect = Channel<SendMoneyEffect>(Channel.BUFFERED)
    val effect: Flow<SendMoneyEffect> = _effect.receiveAsFlow()

    fun onEvent(event: SendMoneyEvent) {
        when (event) {
            is SendMoneyEvent.KeypadKeyClicked -> applyKey(event.key)
            SendMoneyEvent.BackspaceClicked -> applyBackspace()
            is SendMoneyEvent.Send -> sendMoney(event.recipient, event.amount)
            SendMoneyEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun applyKey(key: String) {
        val current = _state.value.amount
        _state.value = _state.value.copy(
            amount = applyAmountKey(current, key),
            errorMessage = null
        )
    }

    private fun applyBackspace() {
        val current = _state.value.amount
        _state.value = _state.value.copy(
            amount = if (current.length > 1) current.dropLast(1) else "0",
            errorMessage = null
        )
    }

    private fun sendMoney(recipient: String, amount: Double) {
        if (amount <= 0) {
            _state.value = _state.value.copy(errorMessage = "Please enter an amount")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            sendMoneyUseCase(recipient, amount)
                .onSuccess {
                    _state.value = SendMoneyUiState()
                    _effect.send(SendMoneyEffect.NavigateToTransferSuccess(recipient, amount))
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Transfer failed"
                    )
                }
        }
    }

    private fun applyAmountKey(amount: String, key: String): String = when (key) {
        "⌫" -> if (amount.length > 1) amount.dropLast(1) else "0"
        "." -> if (amount.contains(".")) amount else "$amount."
        else -> {
            if (!key.all { it.isDigit() }) return amount
            val parts = amount.split(".")
            val integerPart = parts[0]
            val decimalPart = parts.getOrNull(1)
            when {
                amount == "0" -> key
                decimalPart != null -> if (decimalPart.length >= 2) amount else amount + key
                integerPart.length >= 8 -> amount
                else -> amount + key
            }
        }
    }
}
