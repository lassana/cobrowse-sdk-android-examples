package io.cobrowse.sample

import android.app.Application
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.data.CobrowseSessionDelegate

/**
 * Android application class.
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val cobrowse = CobrowseIO.instance()
        cobrowse.license("trial")
        cobrowse.customData(buildMap<String, String> {
            put(CobrowseIO.USER_EMAIL_KEY, "android@demo.com")
            put(CobrowseIO.DEVICE_NAME_KEY, "Android Demo")
        })
        cobrowse.webviewRedactedViews(arrayOf(
            "#title",
            "#amount",
            "#subtitle",
            "#map"
        ))
        cobrowse.setDelegate(CobrowseSessionDelegate.getInstance())

        cobrowse.start()
    }
}