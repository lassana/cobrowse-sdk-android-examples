package io.cobrowse.sample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.cobrowse.sample.data.LoginRepository
import io.cobrowse.sample.data.TransactionsDataSource
import io.cobrowse.sample.data.TransactionsRepository
import io.cobrowse.sample.ui.login.LoginViewModel
import io.cobrowse.sample.ui.main.AccountViewModel
import io.cobrowse.sample.ui.main.AgentPresentViewModel
import io.cobrowse.sample.ui.main.MainViewModel

/**
 * ViewModel provider factory to instantiate all View-Models uses within the app.
 */
class CobrowseViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(
                    loginRepository = LoginRepository.getInstance()
                ) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository = TransactionsRepository(
                    dataSource = TransactionsDataSource()
                )) as T
            }
            modelClass.isAssignableFrom(AccountViewModel::class.java) -> {
                AccountViewModel(
                    loginRepository = LoginRepository.getInstance()
                ) as T
            }
            modelClass.isAssignableFrom(AgentPresentViewModel::class.java) -> {
                AgentPresentViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}