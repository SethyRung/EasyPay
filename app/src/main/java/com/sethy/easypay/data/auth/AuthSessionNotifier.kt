package com.sethy.easypay.data.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide bus for "the session token is dead" events.
 *
 * The write side is `BaseRepository.safeApiCall` — every protected call
 * that comes back with an UNAUTHORIZED envelope calls [notifyExpired].
 * The read side is `AppSessionViewModel`, which observes [events] and
 * clears local token storage + flips `isAuthenticated` to false so
 * AuthGate routes the user back to Login.
 *
 * `extraBufferCapacity = 1` covers the race where an UNAUTHORIZED
 * response arrives while AppSessionViewModel isn't currently collecting
 * (e.g., the VM hasn't been instantiated yet on cold start); `replay = 0`
 * keeps the flow stateless so old events don't fire on a fresh VM.
 */
@Singleton
class AuthSessionNotifier @Inject constructor() {

    private val _events = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun notifyExpired() {
        _events.tryEmit(Unit)
    }
}
