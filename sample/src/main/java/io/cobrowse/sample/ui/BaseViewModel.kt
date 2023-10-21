package io.cobrowse.sample.ui

import androidx.lifecycle.ViewModel
import io.cobrowse.sample.data.CobrowseSessionDelegate

/**
 * Base View-Model class which provides easy access to Cobrowse.io session notifications.
 */
abstract class BaseViewModel : ViewModel() {

    val cobrowseDelegate: CobrowseSessionDelegate
        get() = CobrowseSessionDelegate.getInstance()

    fun endCobrowseSession() {
        /*
         * TODO This kills a session even if it's an "Agent Presentation" one.
         *  What if agent wants to switch from this device to another,
         *  but keeping the existing session?
         */
        cobrowseDelegate.current.value?.end(null)
    }
}