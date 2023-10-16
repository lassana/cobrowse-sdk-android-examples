package io.cobrowse.sample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.sample.R
import io.cobrowse.sample.data.LoginRepository
import io.cobrowse.sample.data.model.LoggedInUser
import io.cobrowse.sample.ui.BaseViewModel

class AccountViewModel(private val loginRepository: LoginRepository) : BaseViewModel() {

    private val _logoutResult = MutableLiveData<LogoutResult>()
    val logoutResult: LiveData<LogoutResult> = _logoutResult

    val user: LoggedInUser?
        get() = loginRepository.user

    fun logOut() {
        if (loginRepository.logout()) {
            _logoutResult.value = LogoutResult(success = true)
        } else {
            _logoutResult.value = LogoutResult(error = R.string.logout_failed)
        }
    }
}