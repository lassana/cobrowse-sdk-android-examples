package com.example.cobrowsefulldevicedemo

import android.app.Application
import io.cobrowse.CobrowseIO

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        with(CobrowseIO.instance()) {
            license("trial")
            customData(mapOf(CobrowseIO.DEVICE_NAME_KEY to "Full Device Demo"))
            start()
        }
    }
}
