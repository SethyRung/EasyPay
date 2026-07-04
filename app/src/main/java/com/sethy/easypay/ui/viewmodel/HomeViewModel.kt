package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.data.model.FeaturedGame
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

    private val _state = MutableStateFlow(
        HomeUiState(featuredGames = MockFeaturedGames.list)
    )
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect: Flow<HomeEffect> = _effect.receiveAsFlow()

    init {
        load()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> load()
            HomeEvent.StoreClick -> viewModelScope.launch {
                _effect.send(HomeEffect.NavigateToStore)
            }
            HomeEvent.SendMoneyClick -> viewModelScope.launch {
                _effect.send(HomeEffect.NavigateToSendMoney)
            }
            is HomeEvent.TransactionClick -> viewModelScope.launch {
                _effect.send(HomeEffect.NavigateToTransactionDetail(event.id))
            }
            HomeEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
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

private object MockFeaturedGames {
    val list: List<FeaturedGame> = listOf(
        FeaturedGame(
            id = "elden_ring",
            name = "Elden Ring",
            priceMajor = 59.99,
            category = "Action RPG",
            coverColorHex = "#5A4A2E"
        ),
        FeaturedGame(
            id = "hades",
            name = "Hades",
            priceMajor = 24.99,
            category = "Roguelike",
            coverColorHex = "#5C2E2E"
        ),
        FeaturedGame(
            id = "hollow_knight",
            name = "Hollow Knight",
            priceMajor = 14.99,
            category = "Metroidvania",
            coverColorHex = "#2E4A5C"
        ),
        FeaturedGame(
            id = "stardew_valley",
            name = "Stardew Valley",
            priceMajor = 14.99,
            category = "Simulation",
            coverColorHex = "#3E5C2E"
        ),
        FeaturedGame(
            id = "cyberpunk_2077",
            name = "Cyberpunk 2077",
            priceMajor = 49.99,
            category = "Action",
            coverColorHex = "#5C2E4A"
        )
    )
}