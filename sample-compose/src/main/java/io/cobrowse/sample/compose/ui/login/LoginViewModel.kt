package io.cobrowse.sample.compose.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.cobrowse.sample.data.LoginRepository
import io.cobrowse.sample.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val isLoggedIn: Boolean
        get() = loginRepository.isLoggedIn

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            usernameError = if (username.isBlank()) "Username cannot be empty" else null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = if (password.isEmpty()) "Password cannot be empty" else null
        )
    }

    fun login() {
        val username = _uiState.value.username
        val password = _uiState.value.password

        if (username.isBlank() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                usernameError = if (username.isBlank()) "Username cannot be empty" else null,
                passwordError = if (password.isEmpty()) "Password cannot be empty" else null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = loginRepository.login(username, password)
            
            _uiState.value = when (result) {
                is Result.Success -> {
                    _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Login failed"
                    )
                }
            }
        }
    }
}
