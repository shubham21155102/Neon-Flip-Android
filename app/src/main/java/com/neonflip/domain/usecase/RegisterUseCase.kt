package com.neonflip.domain.usecase

import com.neonflip.core.common.Result
import com.neonflip.domain.model.AuthResponse
import com.neonflip.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for registering a new user
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): Result<AuthResponse> {
        // Validate input
        if (username.isBlank()) {
            return Result.error(IllegalArgumentException("Username cannot be empty"), "Username cannot be empty")
        }
        if (username.length < 3) {
            return Result.error(IllegalArgumentException("Username must be at least 3 characters"), "Username must be at least 3 characters")
        }
        if (email.isBlank()) {
            return Result.error(IllegalArgumentException("Email cannot be empty"), "Email cannot be empty")
        }
        if (!isValidEmail(email)) {
            return Result.error(IllegalArgumentException("Invalid email format"), "Invalid email format")
        }
        if (password.isBlank()) {
            return Result.error(IllegalArgumentException("Password cannot be empty"), "Password cannot be empty")
        }
        if (password.length < 6) {
            return Result.error(IllegalArgumentException("Password must be at least 6 characters"), "Password must be at least 6 characters")
        }
        if (!password.any { it.isDigit() }) {
            return Result.error(IllegalArgumentException("Password must contain at least one digit"), "Password must contain at least one digit")
        }

        return authRepository.register(username, email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
