package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.data.model.NotificationType
import com.sethy.easypay.domain.usecase.GetNotificationsUseCase
import com.sethy.easypay.domain.usecase.MarkNotificationReadUseCase
import com.sethy.easypay.ui.state.NotificationTab
import com.sethy.easypay.ui.state.NotificationsEffect
import com.sethy.easypay.ui.state.NotificationsEvent
import com.sethy.easypay.ui.state.NotificationsUiState
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
class NotificationsViewModel @Inject constructor(
    private val getNotifications: GetNotificationsUseCase,
    private val markNotificationRead: MarkNotificationReadUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state: StateFlow<NotificationsUiState> = _state.asStateFlow()

    private val _effect = Channel<NotificationsEffect>(Channel.BUFFERED)
    val effect: Flow<NotificationsEffect> = _effect.receiveAsFlow()

    init {
        load()
    }

    fun onEvent(event: NotificationsEvent) {
        when (event) {
            NotificationsEvent.Load -> load()
            is NotificationsEvent.TabSelected -> {
                _state.value = _state.value.copy(selectedTab = event.tab)
            }
            is NotificationsEvent.NotificationClicked -> {
                markAsRead(event.id)
                viewModelScope.launch {
                    _effect.send(NotificationsEffect.NavigateToTransactionDetail(event.id))
                }
            }
            NotificationsEvent.MarkAllRead -> markAllAsRead()
            NotificationsEvent.Back -> viewModelScope.launch {
                _effect.send(NotificationsEffect.NavigateBack)
            }
            NotificationsEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            getNotifications()
                .onSuccess { notifications ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notifications = notifications
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load notifications"
                    )
                }
        }
    }

    private fun markAsRead(id: String) {
        viewModelScope.launch {
            markNotificationRead(id)
                .onSuccess {
                    val updated = _state.value.notifications.map {
                        if (it.id == id) it.copy(isRead = true) else it
                    }
                    _state.value = _state.value.copy(notifications = updated)
                }
                .onFailure { error ->
                    _effect.send(NotificationsEffect.ShowError(error.message ?: "Failed to mark read"))
                }
        }
    }

    private fun markAllAsRead() {
        viewModelScope.launch {
            _state.value.notifications
                .filter { !it.isRead }
                .forEach { markNotificationRead(it.id) }
            val updated = _state.value.notifications.map { it.copy(isRead = true) }
            _state.value = _state.value.copy(notifications = updated)
        }
    }
}
