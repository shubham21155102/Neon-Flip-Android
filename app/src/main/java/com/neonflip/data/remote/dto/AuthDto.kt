package com.neonflip.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request DTOs
data class LoginRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

// Response DTOs
data class AuthResponseDto(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("token")
    val token: String
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("createdAt")
    val createdAt: String
)

// Score DTOs
data class SubmitScoreRequestDto(
    @SerializedName("score")
    val score: Int
)

data class ScoreDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("createdAt")
    val createdAt: String
)

// Error Response
data class ErrorResponseDto(
    @SerializedName("error")
    val error: String?,
    @SerializedName("message")
    val message: String?
)
