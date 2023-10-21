package io.cobrowse.sample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.CobrowseIO
import io.cobrowse.Session
import io.cobrowse.sample.ui.BaseViewModel
import java.lang.Error

/**
 * View-Model for [AgentPresentFragment].
 */
class AgentPresentViewModel : BaseViewModel() {

    private val _agentSessionResult = MutableLiveData<Pair<Error?, Session?>>()
    val agentSessionResult: LiveData<Pair<Error?, Session?>> = _agentSessionResult

    fun initiateSession(code: String) {
        CobrowseIO.instance().getSession(code) { error, session ->
            _agentSessionResult.value = Pair(error, session)
        }
    }
}