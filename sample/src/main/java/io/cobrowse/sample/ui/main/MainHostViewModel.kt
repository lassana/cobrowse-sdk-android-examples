package io.cobrowse.sample.ui.main

import io.cobrowse.sample.data.LoginRepository
import io.cobrowse.sample.ui.BaseViewModel

/**
 * View-Model for [MainActivity].
 */
class MainHostViewModel(private val loginRepository: LoginRepository) : BaseViewModel() {

    val isLoggedIn: Boolean
        get() = loginRepository.isLoggedIn
}