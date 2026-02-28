package com.neonflip.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neonflip.core.common.Result
import com.neonflip.data.local.TokenStorage
import com.neonflip.domain.model.User
import com.neonflip.domain.usecase.GetCurrentUserUseCase
import com.neonflip.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for global auth state management
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
        // Observe token changes to update auth state
        observeTokenChanges()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is Result.Error -> {
                    _authState.value = AuthState.Unauthenticated
                }
                is Result.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    /**
     * Observe token changes to keep auth state up to date
     */
    private fun observeTokenChanges() {
        viewModelScope.launch {
            tokenStorage.getToken().collect { token ->
                if (token != null && _authState.value !is AuthState.Authenticated) {
                    // Token exists but state isn't authenticated, fetch user
                    refreshAuthState()
                } else if (token == null && _authState.value is AuthState.Authenticated) {
                    // Token was cleared, update state
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    /**
     * Refresh auth state (call after login/register)
     */
    fun refreshAuthState() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is Result.Error -> {
                    _authState.value = AuthState.Unauthenticated
                }
                is Result.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}
