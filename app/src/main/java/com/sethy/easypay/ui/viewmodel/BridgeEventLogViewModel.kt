package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.sethy.easypay.bridge.BridgeController
import com.sethy.easypay.bridge.BridgeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BridgeEventLogViewModel @Inject constructor(
    bridgeController: BridgeController
) : ViewModel() {

    val events: StateFlow<List<BridgeEvent>> = bridgeController.eventLog
}