package com.neonflip.data.repository

import android.util.Log
import com.neonflip.core.common.Result
import com.neonflip.data.local.TokenStorage
import com.neonflip.data.mapper.AuthMapper
import com.neonflip.data.mapper.ErrorMapper
import com.neonflip.data.mapper.UserMapper
import com.neonflip.data.remote.api.AuthApiService
import com.neonflip.data.remote.dto.LoginRequestDto
import com.neonflip.data.remote.dto.RegisterRequestDto
import com.neonflip.domain.model.AuthResponse
import com.neonflip.domain.model.User
import com.neonflip.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Implementation of AuthRepository
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequestDto(username, password)
            Log.d("AuthRepository", "Login request: username=$username")

            val response = authApiService.login(request)

            Log.d("AuthRepository", "Login response: code=${response.code()}, successful=${response.isSuccessful}")
            Log.d("AuthRepository", "Response body: ${response.body()}")
            Log.d("AuthRepository", "Error body: ${response.errorBody()?.string()}")

            if (response.isSuccessful && response.body() != null) {
                val authResponse = AuthMapper.toDomain(response.body()!!)
                Log.d("AuthRepository", "Login successful! Token: ${authResponse.token}")
                // Save token and user ID
                tokenStorage.saveToken(authResponse.token)
                tokenStorage.saveUserId(authResponse.user.id)
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "Login failed. Code: ${response.code()}, Error: $errorBody")
                val errorMessage = if (errorBody != null) {
                    try {
                        val gson = com.google.gson.Gson()
                        val errorDto = gson.fromJson(errorBody, com.neonflip.data.remote.dto.ErrorResponseDto::class.java)
                        ErrorMapper.toDomain(errorDto, response.code()).message
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Failed to parse error body", e)
                        "Login failed: ${response.message()}"
                    }
                } else {
                    "Login failed: ${response.message()}"
                }
                Result.error(Exception(errorMessage), errorMessage)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception", e)
            Result.error(e, e.message)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequestDto(username, email, password)
            val response = authApiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = AuthMapper.toDomain(response.body()!!)
                // Save token and user ID
                tokenStorage.saveToken(authResponse.token)
                tokenStorage.saveUserId(authResponse.user.id)
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val gson = com.google.gson.Gson()
                        val errorDto = gson.fromJson(errorBody, com.neonflip.data.remote.dto.ErrorResponseDto::class.java)
                        ErrorMapper.toDomain(errorDto, response.code()).message
                    } catch (e: Exception) {
                        "Registration failed: ${response.message()}"
                    }
                } else {
                    "Registration failed: ${response.message()}"
                }
                Result.error(Exception(errorMessage), errorMessage)
            }
        } catch (e: Exception) {
            Result.error(e, e.message)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = authApiService.getCurrentUser()

            if (response.isSuccessful && response.body() != null) {
                val user = UserMapper.toDomain(response.body()!!)
                Result.success(user)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val gson = com.google.gson.Gson()
                        val errorDto = gson.fromJson(errorBody, com.neonflip.data.remote.dto.ErrorResponseDto::class.java)
                        ErrorMapper.toDomain(errorDto, response.code()).message
                    } catch (e: Exception) {
                        "Failed to get user: ${response.message()}"
                    }
                } else {
                    "Failed to get user: ${response.message()}"
                }
                Result.error(Exception(errorMessage), errorMessage)
            }
        } catch (e: Exception) {
            Result.error(e, e.message)
        }
    }

    override suspend fun logout() {
        tokenStorage.clear()
    }

    override fun isLoggedIn(): Result<Boolean> {
        return runBlocking {
            try {
                val token = tokenStorage.getToken().first()
                Result.success(token != null)
            } catch (e: Exception) {
                Result.error(e, e.message)
            }
        }
    }
}
