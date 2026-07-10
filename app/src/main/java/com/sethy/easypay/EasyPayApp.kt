package com.sethy.easypay

import android.app.Application
import android.webkit.CookieManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EasyPayApp : Application() {

    override fun onCreate() {
        super.onCreate()
        CookieManager.getInstance().setAcceptCookie(true)
    }
}
