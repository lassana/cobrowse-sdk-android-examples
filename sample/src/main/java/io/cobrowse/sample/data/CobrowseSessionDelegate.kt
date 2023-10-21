package io.cobrowse.sample.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.CobrowseIO
import io.cobrowse.Session

/**
 * Global delegate of Cobrowse.io sessions. Stores the current session and provides a way
 * to subscribe to session data updates.
 */
class CobrowseSessionDelegate : CobrowseIO.Delegate {

    private val _current = MutableLiveData<Session?>()
    val current: LiveData<Session?> = _current

    override fun sessionDidUpdate(session: Session) {
        _current.value = session
    }

    override fun sessionDidEnd(session: Session) {
        _current.value = null
    }

    /**
     * Define a companion object, this allows us to add functions on the CobrowseSessionDelegate class.
     */
    companion object {
        /**
         * INSTANCE will keep a reference to any delegate returned via getInstance.
         */
        @Volatile
        private var INSTANCE: CobrowseSessionDelegate? = null

        /**
         * Helper function to get the delegate.
         */
        fun getInstance(): CobrowseSessionDelegate {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = CobrowseSessionDelegate()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}