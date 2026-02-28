package com.neonflip.data.remote.api

import com.neonflip.data.remote.dto.AuthResponseDto
import com.neonflip.data.remote.dto.LoginRequestDto
import com.neonflip.data.remote.dto.RegisterRequestDto
import com.neonflip.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * API Service for Authentication endpoints
 */
interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<UserDto>
}
