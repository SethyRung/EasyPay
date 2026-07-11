package com.sethy.easypay.bridge

interface WebViewCookieStore {
    fun set(host: String, cookieValue: String)
}