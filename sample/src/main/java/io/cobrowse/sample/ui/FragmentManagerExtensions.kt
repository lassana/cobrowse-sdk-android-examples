package io.cobrowse.sample.ui

import android.view.View
import androidx.fragment.app.FragmentManager
import io.cobrowse.CobrowseIO


fun FragmentManager.collectCobrowseRedactedViews(): MutableList<View> {
    return this.fragments
        .filter { it is CobrowseIO.Redacted }
        .flatMap { (it as CobrowseIO.Redacted).redactedViews() }
        .filterNotNull()
        .toMutableList()
}