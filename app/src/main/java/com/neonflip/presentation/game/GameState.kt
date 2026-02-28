package com.neonflip.presentation.game

import com.neonflip.domain.model.GravityDirection
import com.neonflip.domain.model.Obstacle
import com.neonflip.domain.model.PlayerState

/**
 * Game state for the physics engine
 */
data class GameEngineState(
    val player: PlayerState = PlayerState(
        x = 200f,
        y = 500f
    ),
    val gravityDirection: GravityDirection = GravityDirection.DOWN,
    val obstacles: List<Obstacle> = emptyList(),
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val isAutoPlay: Boolean = false,
    val screenWidth: Float = 1080f,
    val screenHeight: Float = 1920f
) {
    companion object {
        // Game constants
        const val GRAVITY = 0.5f
        const val JUMP_FORCE = 12f
        const val MAX_VELOCITY = 15f
        const val OBSTACLE_SPEED = 5f
        const val OBSTACLE_SPAWN_INTERVAL = 1500 // ms
        const val OBSTACLE_GAP_HEIGHT = 200f
        const val PLAYER_RADIUS = 30f
        const val OBSTACLE_WIDTH = 100f
    }
}
