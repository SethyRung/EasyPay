package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.bridge.BridgeController
import com.sethy.easypay.bridge.BridgeEvent
import com.sethy.easypay.bridge.BridgeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class BridgeSessionUiState(
    val status: BridgeStatus = BridgeStatus.Initializing,
    val merchant: String = "Glitch",
    val eventCount: Int = 0,
    val recentEvents: List<BridgeEvent> = emptyList(),
    val sessionStartedAtMillis: Long? = null
) {
    val isActive: Boolean get() = status is BridgeStatus.Online && sessionStartedAtMillis != null
}

@HiltViewModel
class BridgeSessionViewModel @Inject constructor(
    private val bridgeController: BridgeController
) : ViewModel() {

    val state: StateFlow<BridgeSessionUiState> = combine(
        bridgeController.status,
        bridgeController.sessionStartedAt,
        bridgeController.eventLog
    ) { status, startedAt, log ->
        BridgeSessionUiState(
            status = status,
            merchant = "Glitch",
            eventCount = log.size,
            recentEvents = log.takeLast(5),
            sessionStartedAtMillis = startedAt
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BridgeSessionUiState()
    )
}