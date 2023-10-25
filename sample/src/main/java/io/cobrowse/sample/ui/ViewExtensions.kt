package io.cobrowse.sample.ui

import android.view.View

fun View.onSizeChange(callback: () -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View?,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int,
        ) {
            if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
                callback()
            }
        }
    })
}