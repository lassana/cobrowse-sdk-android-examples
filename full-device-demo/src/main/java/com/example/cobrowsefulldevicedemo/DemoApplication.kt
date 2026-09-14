package com.example.cobrowsefulldevicedemo

import android.app.Application
import io.cobrowse.CobrowseIO

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        with(CobrowseIO.instance()) {
            api("https://staging.cbrws.io/")
            license("e-73R7b1nIeoFQ")
            customData(mapOf(CobrowseIO.DEVICE_NAME_KEY to "Full Device Demo"))
            start()
        }
    }
}
