package io.cobrowse.sample.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.CobrowseIO
import io.cobrowse.Session

/**
 * Global delegate of Cobrowse.io sessions. Stores the current session and provides a way
 * to subscribe to session data updates.
 */
object CobrowseSessionDelegate : CobrowseIO.Delegate {

    private val _current = MutableLiveData<Session?>()
    val current: LiveData<Session?> = _current

    override fun sessionDidUpdate(session: Session) {
        _current.value = session
    }

    override fun sessionDidEnd(session: Session) {
        _current.value = null
    }

    /**
     * Helper function to get the delegate object in a more obvious way.
     */
    fun getInstance(): CobrowseSessionDelegate {
        return this
    }
}