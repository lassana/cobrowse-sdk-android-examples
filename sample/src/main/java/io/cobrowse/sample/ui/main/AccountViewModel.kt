package io.cobrowse.sample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.cobrowse.sample.R
import io.cobrowse.sample.data.LoginRepository
import io.cobrowse.sample.data.model.LoggedInUser

class AccountViewModel(private val loginRepository: LoginRepository) : ViewModel() {

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