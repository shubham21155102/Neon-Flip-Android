package com.neonflip.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neonflip.core.common.Result
import com.neonflip.domain.model.AuthResponse
import com.neonflip.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Register Screen
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
    }

    fun register() {
        // Validate passwords match
        if (_password.value != _confirmPassword.value) {
            _registerState.value = RegisterState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val result = registerUseCase(_username.value, _email.value, _password.value)
            when (result) {
                is Result.Success -> {
                    _registerState.value = RegisterState.Success(result.data)
                }
                is Result.Error -> {
                    _registerState.value = RegisterState.Error(
                        result.message ?: "Registration failed"
                    )
                }
                is Result.Loading -> {
                    _registerState.value = RegisterState.Loading
                }
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
        _username.value = ""
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
    }
}

sealed class RegisterState {
    data object Idle : RegisterState()
    data object Loading : RegisterState()
    data class Success(val authResponse: AuthResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
