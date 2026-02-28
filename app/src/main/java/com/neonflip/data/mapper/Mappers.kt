package com.neonflip.data.mapper

import com.neonflip.data.remote.dto.*
import com.neonflip.domain.model.*

/**
 * Mapper for User entities
 */
object UserMapper {
    fun toDomain(dto: UserDto): User = User(
        id = dto.id,
        username = dto.username,
        email = dto.email,
        createdAt = dto.createdAt
    )

    fun toDto(domain: User): UserDto = UserDto(
        id = domain.id,
        username = domain.username,
        email = domain.email,
        createdAt = domain.createdAt
    )
}

/**
 * Mapper for Auth entities
 */
object AuthMapper {
    fun toDomain(dto: AuthResponseDto): AuthResponse = AuthResponse(
        user = UserMapper.toDomain(dto.user),
        token = dto.token
    )

    fun toDto(domain: AuthRequest, isRegister: Boolean = false): Any {
        return if (isRegister) {
            RegisterRequestDto(
                username = domain.username,
                email = domain.email,
                password = domain.password
            )
        } else {
            LoginRequestDto(
                username = domain.username,
                password = domain.password
            )
        }
    }
}

/**
 * Mapper for Score entities
 */
object ScoreMapper {
    fun toDomain(dto: ScoreDto): Score = Score(
        id = dto.id,
        userId = dto.userId,
        username = dto.username,
        score = dto.score,
        createdAt = dto.createdAt
    )

    fun toDto(domain: Score): ScoreDto = ScoreDto(
        id = domain.id,
        userId = domain.userId,
        username = domain.username,
        score = domain.score,
        createdAt = domain.createdAt
    )
}

/**
 * Mapper for Error entities
 */
object ErrorMapper {
    fun toDomain(dto: ErrorResponseDto, code: Int? = null): ApiError = ApiError(
        message = dto.message ?: dto.error ?: "An unknown error occurred",
        code = code,
        error = dto.error
    )
}
