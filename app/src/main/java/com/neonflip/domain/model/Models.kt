package com.neonflip.domain.model

// Auth Models
data class AuthRequest(
    val username: String,
    val email: String = "",
    val password: String
)

data class AuthResponse(
    val user: User,
    val token: String
)

data class User(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: String
)

// Score Models
data class Score(
    val id: String,
    val userId: String,
    val username: String,
    val score: Int,
    val createdAt: String
)

// Error Model
data class ApiError(
    val message: String,
    val code: Int? = null,
    val error: String? = null
)

// Game State Models
enum class GravityDirection {
    UP,
    DOWN
}

data class Obstacle(
    val x: Float,
    val width: Float,
    val gapY: Float,
    val gapHeight: Float,
    val passed: Boolean = false
) {
    companion object {
        const val DEFAULT_WIDTH = 100f
        const val DEFAULT_GAP_HEIGHT = 200f
        const val DEFAULT_OBSTACLE_SPEED = 5f
    }
}

data class GameScore(
    val currentScore: Int = 0,
    val highScore: Int = 0,
    val isNewHighScore: Boolean = false
)

data class PlayerState(
    val x: Float,
    val y: Float,
    val velocityY: Float = 0f,
    val radius: Float = 30f
) {
    companion object {
        const val DEFAULT_RADIUS = 30f
        const val DEFAULT_GRAVITY = 0.5f
        const val DEFAULT_JUMP_FORCE = 12f
        const val MAX_VELOCITY = 15f
    }
}
