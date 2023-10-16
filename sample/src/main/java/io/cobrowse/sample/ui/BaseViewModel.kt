package io.cobrowse.sample.ui

import androidx.lifecycle.ViewModel
import io.cobrowse.sample.data.CobrowseSessionDelegate

abstract class BaseViewModel : ViewModel() {

    val cobrowseDelegate: CobrowseSessionDelegate
        get() = CobrowseSessionDelegate.getInstance()

    fun endCobrowseSession() {
        cobrowseDelegate.current.value?.end(null)
    }
}