package io.cobrowse.sample.ui

import android.view.View
import androidx.fragment.app.FragmentManager
import io.cobrowse.CobrowseIO

/**
 * Finds all fragments that implement [CobrowseIO.Redacted] and collects all redacted views
 * from them.
 */
fun FragmentManager.collectCobrowseRedactedViews(): MutableList<View> {
    return this.fragments
        .filter { it is CobrowseIO.Redacted }
        .flatMap { (it as CobrowseIO.Redacted).redactedViews() }
        .filterNotNull()
        .toMutableList()
}

/**
 * Finds all fragments that implement [CobrowseIO.Unredacted] and collects all unredacted views
 * from them.
 */
fun FragmentManager.collectCobrowseUnredactedViews(): MutableList<View> {
    return this.fragments
        .filter { it is CobrowseIO.Unredacted }
        .flatMap { (it as CobrowseIO.Unredacted).unredactedViews() }
        .filterNotNull()
        .toMutableList()
}
