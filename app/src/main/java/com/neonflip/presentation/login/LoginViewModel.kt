package com.neonflip.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neonflip.core.common.Result
import com.neonflip.domain.model.AuthResponse
import com.neonflip.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Login Screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun login() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = loginUseCase(_username.value, _password.value)
            when (result) {
                is Result.Success -> {
                    _loginState.value = LoginState.Success(result.data)
                }
                is Result.Error -> {
                    _loginState.value = LoginState.Error(
                        result.message ?: "Login failed"
                    )
                }
                is Result.Loading -> {
                    _loginState.value = LoginState.Loading
                }
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
        _username.value = ""
        _password.value = ""
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val authResponse: AuthResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}
