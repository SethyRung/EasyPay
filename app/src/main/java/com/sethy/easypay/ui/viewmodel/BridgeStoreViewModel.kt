package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.bridge.BridgeController
import com.sethy.easypay.bridge.BridgeStatus
import com.sethy.easypay.bridge.PaymentSheetState
import com.sethy.easypay.bridge.StoreEntryBridge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BridgeStoreViewModel @Inject constructor(
    val bridgeController: BridgeController,
    private val storeEntryBridge: StoreEntryBridge,
) : ViewModel() {

    val status: StateFlow<BridgeStatus> = bridgeController.status
    val paymentSheetState: StateFlow<PaymentSheetState> = bridgeController.paymentSheetState

    private val _storeUrl = MutableStateFlow("")
    val storeUrl: StateFlow<String> = _storeUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showCloseDialog = MutableStateFlow(false)
    val showCloseDialog: StateFlow<Boolean> = _showCloseDialog.asStateFlow()

    init {
        openStore()
    }

    fun openStore() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            storeEntryBridge.openStore()
                .onSuccess { host ->
                    _storeUrl.value = host
                }
                .onFailure { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message ?: "Failed to enter store"
                }
        }
    }

    fun onLoaded() {
        _isLoading.value = false
        _errorMessage.value = null
    }

    fun onBackPressed() {
        _showCloseDialog.value = true
    }

    fun dismissCloseDialog() {
        _showCloseDialog.value = false
    }

    fun onLoadError(message: String) {
        _isLoading.value = false
        _errorMessage.value = message
        bridgeController.markOffline(message)
    }

    fun confirmPayment() {
        bridgeController.confirmPayment()
    }

    fun declinePayment() {
        bridgeController.declinePayment()
    }

    fun onPaymentSheetDismissed() {
        bridgeController.dismissPaymentSheet()
    }

    fun dismissPaymentSheet() {
        bridgeController.dismissPaymentSheet()
    }
}