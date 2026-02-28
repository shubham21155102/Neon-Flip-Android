package com.neonflip.presentation.game

import android.util.Log
import com.neonflip.domain.model.GravityDirection
import com.neonflip.domain.model.Obstacle
import com.neonflip.domain.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

import android.content.Context

/**
 * Game Physics Engine
 * Handles game loop, physics updates, collision detection, and obstacle spawning
 */
class GameEngine(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onGameOver: () -> Unit = {},
    private val onScoreUpdate: (Int) -> Unit = {}
) {
    private val soundManager = SoundManager(context)
    private val _gameState = MutableStateFlow(createInitialState())
    val gameState: StateFlow<GameEngineState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var obstacleSpawnJob: Job? = null
    private val random = Random

    fun setScreenSize(width: Float, height: Float) {
        _gameState.value = _gameState.value.copy(
            screenWidth = width,
            screenHeight = height
        )
    }

    fun startGame(isAutoPlay: Boolean = false) {
        Log.d("GameEngine", "Game started. Autoplay: $isAutoPlay")
        // Cancel any existing jobs before starting new ones
        gameLoopJob?.cancel()
        obstacleSpawnJob?.cancel()
        _gameState.value = createInitialState(isAutoPlay)
        startGameLoop()
        startObstacleSpawner()
    }

    fun pauseGame() {
        gameLoopJob?.cancel()
        obstacleSpawnJob?.cancel()
    }

    fun cleanup() {
        soundManager.release()
    }

    fun resumeGame() {
        if (gameLoopJob?.isActive != true) {
            startGameLoop()
        }
        if (obstacleSpawnJob?.isActive != true) {
            startObstacleSpawner()
        }
    }

    fun flipGravity() {
        val currentGravityDirection = _gameState.value.gravityDirection

        // Apply immediate impulse against the current gravity direction
        val newVelocityY = if (currentGravityDirection == GravityDirection.DOWN) {
            // Gravity is pulling down, so apply upward impulse (negative velocity)
            -GameEngineState.JUMP_FORCE
        } else {
            // Gravity is pulling up, so apply downward impulse (positive velocity)
            GameEngineState.JUMP_FORCE
        }

        // Flip the gravity direction
        val newGravityDirection = if (currentGravityDirection == GravityDirection.DOWN) {
            GravityDirection.UP
        } else {
            GravityDirection.DOWN
        }

        soundManager.playJumpSound()
        
        _gameState.value = _gameState.value.copy(
            gravityDirection = newGravityDirection,
            player = _gameState.value.player.copy(velocityY = newVelocityY)
        )
    }

    private fun createInitialState(isAutoPlay: Boolean = false): GameEngineState {
        return GameEngineState(
            player = PlayerState(
                x = 200f,
                y = 500f,
                radius = GameEngineState.PLAYER_RADIUS
            ),
            gravityDirection = GravityDirection.DOWN,
            obstacles = emptyList(),
            score = 0,
            isGameOver = false,
            isAutoPlay = isAutoPlay
        )
    }

    private fun startGameLoop() {
        gameLoopJob = scope.launch {
            while (true) {
                update()
                delay(16) // ~60 FPS
            }
        }
    }

    private fun startObstacleSpawner() {
        obstacleSpawnJob = scope.launch {
            while (true) {
                delay(GameEngineState.OBSTACLE_SPAWN_INTERVAL.toLong())
                spawnObstacle()
            }
        }
    }

    private fun spawnObstacle() {
        val state = _gameState.value
        val gapY = random.nextInt(
            (state.screenHeight * 0.2f).toInt(),
            (state.screenHeight * 0.8f).toInt()
        ).toFloat()

        // Dynamic difficulty: Gap starts at 500f and shrinks by 15f per point, down to a minimum of 200f
        val dynamicGapHeight = max(
            GameEngineState.OBSTACLE_GAP_HEIGHT, // The hard limit (200f)
            500f - (state.score * 15f)
        )

        val newObstacle = Obstacle(
            x = state.screenWidth + 50f,
            width = GameEngineState.OBSTACLE_WIDTH,
            gapY = gapY,
            gapHeight = dynamicGapHeight
        )

        _gameState.value = state.copy(
            obstacles = state.obstacles + newObstacle
        )
    }

    private fun update() {
        val state = _gameState.value
        if (state.isGameOver) return

        // Autoplay AI Logic
        if (state.isAutoPlay) {
            handleAutoPlayLogic(state)
        }

        // Update player physics
        val updatedPlayer = updatePlayerPhysics(state.player, state.gravityDirection, state.screenHeight)

        // Update obstacles and get newly passed count
        val (updatedObstacles, newlyPassed) = updateObstacles(state.obstacles)

        // Check collisions
        val collisionDetected = checkCollisions(updatedPlayer, updatedObstacles)

        // Calculate score
        val newScore = state.score + newlyPassed

        val newState = state.copy(
            player = updatedPlayer,
            obstacles = updatedObstacles,
            score = newScore,
            isGameOver = collisionDetected
        )

        _gameState.value = newState

        if (collisionDetected) {
            soundManager.playGameOverSound()
            pauseGame()
            onGameOver()
        } else if (newScore != state.score && newScore > state.score) {
            soundManager.playScoreSound()
            onScoreUpdate(newScore)
        }
    }

    private fun updatePlayerPhysics(
        player: PlayerState,
        gravityDirection: GravityDirection,
        screenHeight: Float
    ): PlayerState {
        // Apply gravity
        val gravityForce = if (gravityDirection == GravityDirection.DOWN) {
            GameEngineState.GRAVITY
        } else {
            -GameEngineState.GRAVITY
        }

        var newVelocityY = player.velocityY + gravityForce
        newVelocityY = max(-GameEngineState.MAX_VELOCITY, min(GameEngineState.MAX_VELOCITY, newVelocityY))

        var newY = player.y + newVelocityY

        // Boundary checks (top and bottom)
        if (newY - player.radius < 0) {
            newY = player.radius
            newVelocityY = 0f
        }
        if (newY + player.radius > screenHeight) {
            newY = screenHeight - player.radius
            newVelocityY = 0f
        }

        return player.copy(
            y = newY,
            velocityY = newVelocityY
        )
    }

    private fun updateObstacles(obstacles: List<Obstacle>): Pair<List<Obstacle>, Int> {
        var newlyPassed = 0
        val updated = obstacles
            .map { obstacle ->
                val newX = obstacle.x - GameEngineState.OBSTACLE_SPEED
                var isPassed = obstacle.passed

                // Player is at x = 200f
                if (!isPassed && newX + obstacle.width < 200f) {
                    isPassed = true
                    newlyPassed++
                }

                obstacle.copy(x = newX, passed = isPassed)
            }
            .filter { it.x + it.width > 0 } // Remove off-screen obstacles
            
        return Pair(updated, newlyPassed)
    }

    private fun checkCollisions(player: PlayerState, obstacles: List<Obstacle>): Boolean {
        for (obstacle in obstacles) {
            // Check if player is within obstacle x range
            if (player.x + player.radius > obstacle.x &&
                player.x - player.radius < obstacle.x + obstacle.width) {

                // Check if player is NOT in the gap
                val playerTop = player.y - player.radius
                val playerBottom = player.y + player.radius
                val gapTop = obstacle.gapY
                val gapBottom = obstacle.gapY + obstacle.gapHeight

                // Collision if player overlaps with obstacle (not in gap)
                if (!(playerBottom > gapTop && playerTop < gapBottom)) {
                    return true
                }
            }
        }
        return false
    }


    private fun handleAutoPlayLogic(state: GameEngineState) {
        // Find the next closest obstacle
        val nextObstacle = state.obstacles.firstOrNull { it.x + it.width > state.player.x }

        if (nextObstacle != null) {
            // Target is the center of the gap
            val targetY = nextObstacle.gapY + (nextObstacle.gapHeight / 2)
            val diffY = state.player.y - targetY

            // Simple AI: If we are drifting too far from the center, flip gravity
            if (state.gravityDirection == GravityDirection.DOWN && diffY > 50f) {
                // Gravity pulling us down, but we are below the target
                flipGravity()
            } else if (state.gravityDirection == GravityDirection.UP && diffY < -50f) {
                // Gravity pulling us up, but we are above the target
                flipGravity()
            }
        }
    }
}
