package com.sethy.easypay.bridge

import com.sethy.easypay.domain.usecase.GetBalanceUseCase
import com.sethy.easypay.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import top.sunhy.component.jsbridge.IBridgeHandler

class BridgeControllerTest {

    private fun createController(
        handler: IBridgeHandler = mock()
    ): BridgeController {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        val getBalance: GetBalanceUseCase = mock()
        return BridgeController(
            getCurrentUser,
            getBalance,
            BridgeHandlerFactory { handler }
        )
    }

    @Test
    fun initial_status_is_Initializing() = runTest {
        val controller = createController()
        assertEquals(BridgeStatus.Initializing, controller.status.value)
    }

    @Test
    fun markOffline_transitions_status_to_Offline() = runTest {
        val controller = createController()

        controller.markOffline("Glitch dev server is down")

        val status = controller.status.value
        assertTrue(status is BridgeStatus.Offline)
        assertEquals("Glitch dev server is down", (status as BridgeStatus.Offline).reason)
    }

    @Test
    fun detached_resets_status_to_Initializing() = runTest {
        val controller = createController()

        controller.markOffline("connection lost")
        controller.detach()

        assertEquals(BridgeStatus.Initializing, controller.status.value)
    }

    @Test
    fun attach_then_detach_round_trip_returns_to_Initializing() = runTest {
        val handler: IBridgeHandler = mock()
        val controller = createController(handler)

        controller.markOffline("manual")
        controller.attach(mock())
        assertEquals(BridgeStatus.Online, controller.status.value)
        controller.detach()
        assertEquals(BridgeStatus.Initializing, controller.status.value)
    }
}