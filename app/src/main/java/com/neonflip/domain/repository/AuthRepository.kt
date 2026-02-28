package com.neonflip.domain.repository

import com.neonflip.core.common.Result
import com.neonflip.domain.model.AuthRequest
import com.neonflip.domain.model.AuthResponse
import com.neonflip.domain.model.User

/**
 * Repository interface for Authentication operations
 */
interface AuthRepository {
    /**
     * Login with username and password
     */
    suspend fun login(username: String, password: String): Result<AuthResponse>

    /**
     * Register a new user
     */
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse>

    /**
     * Get the currently authenticated user
     */
    suspend fun getCurrentUser(): Result<User>

    /**
     * Logout the current user
     */
    suspend fun logout()

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Result<Boolean>
}
