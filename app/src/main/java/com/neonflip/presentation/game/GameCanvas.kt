package com.neonflip.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.neonflip.ui.theme.NeonCyan
import com.neonflip.ui.theme.NeonGreen
import com.neonflip.ui.theme.NeonPink
import com.neonflip.ui.theme.NeonPurple
import com.neonflip.ui.theme.NeonYellow
import com.neonflip.ui.theme.NeonBlue
import com.neonflip.domain.model.GravityDirection
import kotlin.math.sin

/**
 * Custom Canvas for rendering the game
 */
@Composable
fun GameCanvas(
    gameState: GameEngineState,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    onTap()
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Draw background
        drawRect(color = Color(0xFF050510))

        // Time-based animation factor using current time
        val timeMillis = System.currentTimeMillis()
        val pulseAnimation = (sin(timeMillis / 200.0) * 0.5 + 0.5).toFloat() // 0f to 1f
        
        // Draw Scrolling Neon Grid Background
        val gridSize = 100f
        val scrollOffset = (timeMillis / 10f) % gridSize
        
        // Vertical grid lines
        for (i in 0..(canvasWidth / gridSize).toInt() + 1) {
            val lineX = i * gridSize - scrollOffset
            drawLine(
                color = NeonCyan.copy(alpha = 0.15f),
                start = Offset(lineX, 0f),
                end = Offset(lineX, canvasHeight),
                strokeWidth = 2f
            )
        }
        
        // Horizontal grid lines
        for (i in 0..(canvasHeight / gridSize).toInt() + 1) {
            val lineY = i * gridSize
            drawLine(
                color = NeonCyan.copy(alpha = 0.15f),
                start = Offset(0f, lineY),
                end = Offset(canvasWidth, lineY),
                strokeWidth = 2f
            )
        }



        // Alternating colors palette
        val obstacleColors = listOf(
            NeonPink to NeonPurple,
            NeonCyan to NeonBlue,
            NeonGreen to NeonYellow
        )

        // Draw obstacles
        gameState.obstacles.forEachIndexed { index, obstacle ->
            val colorPair = obstacleColors[index % obstacleColors.size]
            val glowColor = colorPair.first
            val coreColor = colorPair.second

            val x = obstacle.x / gameState.screenWidth * canvasWidth
            
            // Add pulsating effect to width
            val baseWidth = obstacle.width / gameState.screenWidth * canvasWidth
            val animatedWidth = baseWidth + (pulseAnimation * 10f)
            val offsetX = x - ((animatedWidth - baseWidth) / 2f)

            val gapY = obstacle.gapY / gameState.screenHeight * canvasHeight
            val gapHeight = obstacle.gapHeight / gameState.screenHeight * canvasHeight

            // Draw Top Obstacle Glow
            drawRect(
                color = glowColor.copy(alpha = 0.4f),
                topLeft = Offset(offsetX - 15f, 0f),
                size = Size(animatedWidth + 30f, gapY)
            )

            // Draw Top Obstacle Core
            drawRect(
                color = coreColor,
                topLeft = Offset(offsetX, 0f),
                size = Size(animatedWidth, gapY)
            )
            
            // Draw Top True White Core
            drawRect(
                color = Color.White,
                topLeft = Offset(offsetX + (animatedWidth * 0.3f), 0f),
                size = Size(animatedWidth * 0.4f, gapY)
            )

            // Draw Bottom Obstacle Glow
            drawRect(
                color = glowColor.copy(alpha = 0.4f),
                topLeft = Offset(offsetX - 15f, gapY + gapHeight),
                size = Size(animatedWidth + 30f, canvasHeight - gapY - gapHeight)
            )

            // Draw Bottom Obstacle Core
            drawRect(
                color = coreColor,
                topLeft = Offset(offsetX, gapY + gapHeight),
                size = Size(animatedWidth, canvasHeight - gapY - gapHeight)
            )
            
            // Draw Bottom True White Core
            drawRect(
                color = Color.White,
                topLeft = Offset(offsetX + (animatedWidth * 0.3f), gapY + gapHeight),
                size = Size(animatedWidth * 0.4f, canvasHeight - gapY - gapHeight)
            )
        }

        // Draw player
        val playerX = gameState.player.x / gameState.screenWidth * canvasWidth
        val playerY = gameState.player.y / gameState.screenHeight * canvasHeight
        val playerRadius = gameState.player.radius / gameState.screenWidth * canvasWidth

        // Dynamic motion stretch based on vertical velocity
        val maxVelocity = GameEngineState.MAX_VELOCITY
        val stretchFactor = 1f + (kotlin.math.abs(gameState.player.velocityY) / maxVelocity) * 0.8f
        val squashFactor = 1f / (1f + (kotlin.math.abs(gameState.player.velocityY) / maxVelocity) * 0.3f)
        
        val playerHeight = playerRadius * 2 * stretchFactor
        val playerWidth = playerRadius * 2 * squashFactor

        // Draw Player Outer Glow (Motion Blur Trail)
        drawOval(
            color = NeonCyan.copy(alpha = 0.4f),
            topLeft = Offset(playerX - (playerWidth * 0.75f), playerY - (playerHeight * 0.75f)),
            size = Size(playerWidth * 1.5f, playerHeight * 1.5f)
        )

        // Draw Player Core
        drawOval(
            color = NeonCyan,
            topLeft = Offset(playerX - (playerWidth / 2f), playerY - (playerHeight / 2f)),
            size = Size(playerWidth, playerHeight)
        )

        // Draw True White Core
        drawOval(
            color = Color.White,
            topLeft = Offset(playerX - (playerWidth * 0.25f), playerY - (playerHeight * 0.25f)),
            size = Size(playerWidth * 0.5f, playerHeight * 0.5f)
        )

        // Draw gravity indicator
        val indicatorSize = 20f
        val indicatorX = canvasWidth - 50f
        val indicatorY = 50f

        if (gameState.gravityDirection == GravityDirection.UP) {
            // Up arrow
            drawLine(
                color = NeonGreen,
                start = Offset(indicatorX, indicatorY + indicatorSize),
                end = Offset(indicatorX, indicatorY - indicatorSize),
                strokeWidth = 4f
            )
            drawLine(
                color = NeonGreen,
                start = Offset(indicatorX - indicatorSize / 2, indicatorY - indicatorSize / 2),
                end = Offset(indicatorX, indicatorY - indicatorSize),
                strokeWidth = 4f
            )
            drawLine(
                color = NeonGreen,
                start = Offset(indicatorX + indicatorSize / 2, indicatorY - indicatorSize / 2),
                end = Offset(indicatorX, indicatorY - indicatorSize),
                strokeWidth = 4f
            )
        } else {
            // Down arrow
            drawLine(
                color = NeonGreen,
                start = Offset(indicatorX, indicatorY - indicatorSize),
                end = Offset(indicatorX, indicatorY + indicatorSize),
                strokeWidth = 4f
            )
            drawLine(
                color = NeonGreen,
                start = Offset(indicatorX - indicatorSize / 2, indicatorY + indicatorSize / 2),
                end = Offset(indicatorX, indicatorY + indicatorSize),
                strokeWidth = 4f
            )
            drawLine(
                color = NeonGreen,
                start = Offset(indicatorX + indicatorSize / 2, indicatorY + indicatorSize / 2),
                end = Offset(indicatorX, indicatorY + indicatorSize),
                strokeWidth = 4f
            )
        }
    }
}
