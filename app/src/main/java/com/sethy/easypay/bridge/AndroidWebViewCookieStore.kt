package com.sethy.easypay.bridge

import android.webkit.CookieManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidWebViewCookieStore @Inject constructor() : WebViewCookieStore {

    override fun set(host: String, cookieValue: String) {
        CookieManager.getInstance().apply {
            setCookie(host, cookieValue)
            flush()
        }
    }
}