package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.domain.usecase.GetTransactionUseCase
import com.sethy.easypay.ui.state.TransactionDetailEffect
import com.sethy.easypay.ui.state.TransactionDetailEvent
import com.sethy.easypay.ui.state.TransactionDetailUiState
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
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransaction: GetTransactionUseCase
) : ViewModel() {

    private val transactionId: String = savedStateHandle["id"] ?: ""

    private val _state = MutableStateFlow(TransactionDetailUiState())
    val state: StateFlow<TransactionDetailUiState> = _state.asStateFlow()

    private val _effect = Channel<TransactionDetailEffect>(Channel.BUFFERED)
    val effect: Flow<TransactionDetailEffect> = _effect.receiveAsFlow()

    init {
        load()
    }

    fun onEvent(event: TransactionDetailEvent) {
        when (event) {
            TransactionDetailEvent.Load -> load()
            TransactionDetailEvent.Refresh -> load()
            TransactionDetailEvent.ShareReceipt -> viewModelScope.launch {
                _effect.send(TransactionDetailEffect.ShareReceipt)
            }
            TransactionDetailEvent.ReportIssue -> viewModelScope.launch {
                _effect.send(TransactionDetailEffect.ShowError("Report issue not yet implemented"))
            }
            TransactionDetailEvent.Back -> viewModelScope.launch {
                _effect.send(TransactionDetailEffect.NavigateBack)
            }
            TransactionDetailEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun load() {
        if (transactionId.isBlank()) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Invalid transaction id"
            )
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            getTransaction(transactionId)
                .onSuccess { transaction ->
                    _state.value = TransactionDetailUiState(
                        isLoading = false,
                        transaction = transaction
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load transaction"
                    )
                }
        }
    }
}
