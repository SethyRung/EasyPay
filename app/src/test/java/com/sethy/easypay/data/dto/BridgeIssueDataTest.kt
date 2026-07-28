package com.sethy.easypay.data.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class BridgeIssueDataTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun ticket_field_deserializes_from_envelope_data_shape() {
        val wire = """{"status":{"code":"OK","message":"ok","requestId":"r","requestTime":0},"data":{"ticket":"abc"},"meta":null}"""

        val envelope = json.decodeFromString<ApiResponse<BridgeIssueData>>(wire)

        assertEquals("abc", envelope.data?.ticket)
    }

    @Test
    fun ticket_field_deserializes_from_bare_data_class_shape() {
        val wire = """{"ticket":"abc"}"""

        val parsed = json.decodeFromString<BridgeIssueData>(wire)

        assertEquals("abc", parsed.ticket)
    }
}