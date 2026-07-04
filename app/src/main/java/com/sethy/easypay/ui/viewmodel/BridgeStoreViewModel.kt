package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.sethy.easypay.bridge.BridgeController
import com.sethy.easypay.bridge.BridgeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BridgeStoreViewModel @Inject constructor(
    val bridgeController: BridgeController
) : ViewModel() {

    val status: StateFlow<BridgeStatus> = bridgeController.status

    private val _storeUrl = MutableStateFlow(DEFAULT_STORE_URL)
    val storeUrl: StateFlow<String> = _storeUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onLoaded() {
        _isLoading.value = false
        _errorMessage.value = null
    }

    fun onLoadError(message: String) {
        _isLoading.value = false
        _errorMessage.value = message
        bridgeController.markOffline(message)
    }

    companion object {
        const val DEFAULT_STORE_URL = "http://10.0.2.2:3000/"
    }
}