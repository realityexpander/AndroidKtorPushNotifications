package com.realityexpander.ktorpushnotifications

import android.app.Application
import com.onesignal.OneSignal

class PushApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }

    companion object {
        private const val ONESIGNAL_APP_ID = "01206360-f412-41b8-b4ed-830f026b4660"
    }
}