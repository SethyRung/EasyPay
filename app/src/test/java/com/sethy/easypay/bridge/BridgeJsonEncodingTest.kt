package com.sethy.easypay.bridge

import com.sethy.easypay.data.model.User
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression tests for JSON payload construction inside [BridgeController].
 *
 * The controller hand-builds JSON that gets parsed by Glitch's WebViewJavascriptBridge.
 * Any failure to escape user-controlled values (name, email, note, merchant ref,
 * error message) is a JSON-injection vector. These tests verify that
 * payloads round-trip through [JSONObject] without throwing and preserve
 * the original Unicode/control-character content.
 *
 * The encode/failure-payload helpers are exposed as `internal` on
 * [BridgeController] purely so they can be unit-tested without going
 * through the suspend / WebView plumbing of the public handler flow.
 */
class BridgeJsonEncodingTest {

    @Test
    fun encodeBridgeUser_escapesDoubleQuoteInName() {
        val payload = BridgeController.encodeBridgeUser(
            User(id = "u1", name = """evil"name""", email = "e@x.com")
        )

        val parsed = JSONObject(payload)
        assertEquals("u1", parsed.getString("id"))
        assertEquals("""evil"name""", parsed.getString("name"))
        assertEquals("e@x.com", parsed.getString("email"))
    }

    @Test
    fun encodeBridgeUser_preservesNewlineAndTab() {
        val payload = BridgeController.encodeBridgeUser(
            User(id = "u1", name = "line1\nline2\ttabbed", email = "e@x.com")
        )

        val parsed = JSONObject(payload)
        assertEquals("line1\nline2\ttabbed", parsed.getString("name"))
    }

    @Test
    fun encodeBridgeUser_escapesBackslash() {
        val payload = BridgeController.encodeBridgeUser(
            User(id = "u1", name = """evil\name""", email = "e@x.com")
        )

        val parsed = JSONObject(payload)
        assertEquals("""evil\name""", parsed.getString("name"))
    }

    @Test
    fun encodeBridgeUser_preservesUnicodeAndEmoji() {
        val payload = BridgeController.encodeBridgeUser(
            User(id = "u1", name = "Séthy 🎮 Player", email = "e@x.com")
        )

        val parsed = JSONObject(payload)
        assertEquals("Séthy 🎮 Player", parsed.getString("name"))
    }

    @Test
    fun failurePayload_escapesDoubleQuoteInErrorMessage() {
        val payload = BridgeController.failurePayload(
            method = "wallet.getBalance",
            code = "NETWORK",
            message = """boom "quoted" message"""
        )

        val parsed = JSONObject(payload)
        assertEquals(false, parsed.getBoolean("ok"))
        val error = parsed.getJSONObject("error")
        assertEquals("wallet.getBalance", error.getString("method"))
        assertEquals("NETWORK", error.getString("code"))
        assertEquals("""boom "quoted" message""", error.getString("message"))
    }

    @Test
    fun failurePayload_neutralisesScriptInjectionAttempt() {
        val payload = BridgeController.failurePayload(
            method = "wallet.getBalance",
            code = "NETWORK",
            message = "</script><script>alert(1)</script>"
        )

        // If the payload were not properly escaped, JSON parsing would throw.
        // The fact that this parses cleanly — and that the round-tripped message
        // contains the original "<script>" markers — proves the value stayed
        // inside its JSON string and never broke out.
        val parsed = JSONObject(payload)
        val message = parsed.getJSONObject("error").getString("message")

        assertEquals("</script><script>alert(1)</script>", message)
    }

    @Test
    fun encodePaymentSuccess_escapesMerchantRefAndTransactionId() {
        val request = BridgePaymentRequest(
            merchantRef = """mer"chant\ref""",
            billerCode = "glitch",
            accountNumber = "acc-1",
            amountMajor = 12.5,
            currency = "USD",
            note = "ignored — not part of the success payload",
            items = emptyList()
        )

        val payload = BridgeController.encodePaymentSuccess(
            request = request,
            balanceAfterMinor = 8750L,
            transactionId = """tx"id"""
        )

        val parsed = JSONObject(payload)
        assertEquals(true, parsed.getBoolean("ok"))
        assertEquals("""mer"chant\ref""", parsed.getString("merchantRef"))
        assertEquals("""tx"id""", parsed.getString("transactionId"))
        assertEquals(1250L, parsed.getLong("amountMinor"))
        assertEquals(8750L, parsed.getLong("balanceAfterMinor"))
    }
}