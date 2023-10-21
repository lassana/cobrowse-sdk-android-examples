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

        with(CobrowseIO.instance()) {
            license("trial")
            customData(buildMap<String, String> {
                put(CobrowseIO.USER_EMAIL_KEY, "android@demo.com")
                put(CobrowseIO.DEVICE_NAME_KEY, "Android Demo")
            })
            webviewRedactedViews(arrayOf(
                "#title",
                "#amount",
                "#subtitle",
                "#map"
            ))
            setDelegate(CobrowseSessionDelegate.getInstance())
            start()
        }
    }
}