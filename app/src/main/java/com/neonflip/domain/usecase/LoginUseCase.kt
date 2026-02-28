package com.neonflip.domain.usecase

import com.neonflip.core.common.Result
import com.neonflip.domain.model.AuthResponse
import com.neonflip.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging in a user
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<AuthResponse> {
        // Validate input
        if (username.isBlank()) {
            return Result.error(IllegalArgumentException("Username cannot be empty"), "Username cannot be empty")
        }
        if (password.isBlank()) {
            return Result.error(IllegalArgumentException("Password cannot be empty"), "Password cannot be empty")
        }
        if (password.length < 6) {
            return Result.error(IllegalArgumentException("Password must be at least 6 characters"), "Password must be at least 6 characters")
        }

        return authRepository.login(username, password)
    }
}
