package com.neonflip.presentation.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neonflip.domain.model.User
import com.neonflip.ui.theme.*

/**
 * Professional Animated Game Screen
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    user: User,
    onNavigateToLeaderboard: () -> Unit,
    onLogout: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val currentScore by viewModel.currentScore.collectAsState()
    val highScore by viewModel.highScore.collectAsState()
    val autoPlayCount by viewModel.autoPlayCount.collectAsState()
    val isAutoPlayEnabled by viewModel.isAutoPlayEnabled.collectAsState()

    // Update user in viewModel
    LaunchedEffect(user) {
        viewModel.setUser(user)
    }

    val context = LocalContext.current
    val gameEngine = remember {
        GameEngine(
            context = context,
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
            onScoreUpdate = { newScore -> viewModel.updateScore(newScore) },
            onGameOver = { viewModel.gameOver() }
        )
    }

    val engineState by gameEngine.gameState.collectAsState()

    // Start game when ready
    LaunchedEffect(gameState) {
        if (gameState is GameState.Playing) {
            gameEngine.startGame(isAutoPlay = isAutoPlayEnabled)
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            gameEngine.pauseGame()
            gameEngine.cleanup()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Game Canvas - disable tap when game is over
        GameCanvas(
            gameState = engineState,
            onTap = {
                // Only allow tap during gameplay
                if (gameState is GameState.Playing) {
                    gameEngine.flipGravity()
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top HUD
        AnimatedTopHUD(
            currentScore = currentScore,
            highScore = highScore,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(start = 60.dp, end = 60.dp, top = 16.dp)
        )

        // Logout button (top-left)
        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .alpha(if (gameState is GameState.Playing) 1f else 0f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Logout,
                contentDescription = "Logout",
                tint = ErrorRed,
                modifier = Modifier.size(28.dp)
            )
        }

        // Leaderboard button (top-right)
        IconButton(
            onClick = onNavigateToLeaderboard,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
                .alpha(if (gameState is GameState.Playing) 1f else 0f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = "Leaderboard",
                tint = NeonPink,
                modifier = Modifier.size(32.dp)
            )
        }

        // Game Over Overlay
        when (gameState) {
            is GameState.Ready, is GameState.GameOver, is GameState.Submitting, is GameState.Submitted -> {
                GameOverOverlay(
                    gameState = gameState,
                    currentScore = currentScore,
                    highScore = highScore,
                    autoPlayCount = autoPlayCount,
                    onRestart = {
                        viewModel.startGame()
                    },
                    onAutoplay = {
                        viewModel.startGame(isAutoPlay = true)
                    },
                    onSubmitScore = { viewModel.submitScore() },
                    onNavigateToLeaderboard = onNavigateToLeaderboard,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {}
        }
    }
}

@Composable
fun AnimatedTopHUD(
    currentScore: Int,
    highScore: Int,
    modifier: Modifier = Modifier
) {
    val scoreScale by animateFloatAsState(
        targetValue = if (currentScore > 0) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "score_pulse"
    )

    val isVisible = currentScore != 0 || highScore != 0

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -100 },
            animationSpec = tween(400, easing = EaseOutCubic)
        ) + fadeIn(animationSpec = tween(400, easing = EaseOutCubic)),
        exit = slideOutVertically(
            targetOffsetY = { -100 },
            animationSpec = tween(300, easing = EaseInCubic)
        ) + fadeOut(animationSpec = tween(300, easing = EaseInCubic)),
        modifier = modifier
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ScoreCard
            AnimatedScoreCard(
                label = "SCORE",
                score = currentScore,
                color = NeonCyan,
                modifier = Modifier.scale(scoreScale)
            )

            // High ScoreCard
            AnimatedScoreCard(
                label = "BEST",
                score = highScore,
                color = NeonPink,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun AnimatedScoreCard(
    label: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(300, easing = EaseOutCubic),
        label = "score_$label"
    )

    Card(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A24).copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Text(
                text = animatedScore.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameOverOverlay(
    gameState: GameState,
    currentScore: Int,
    highScore: Int,
    autoPlayCount: Int,
    onRestart: () -> Unit,
    onAutoplay: () -> Unit,
    onSubmitScore: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Wrap in Box to include backdrop
    Box(modifier = modifier.fillMaxSize()) {
        // Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.95f)
                        ),
                        radius = 1200f
                    )
                )
        )

        // Content
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(
                animationSpec = tween(400, easing = EaseOutCubic)
            ) + expandVertically(
                animationSpec = tween(400, easing = EaseOutCubic)
            ),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 28.dp)
        ) {
        when (gameState) {
            is GameState.Ready, is GameState.GameOver -> GameOverContent(
                currentScore = currentScore,
                highScore = highScore,
                autoPlayCount = autoPlayCount,
                gameState = gameState,
                onRestart = onRestart,
                onAutoplay = onAutoplay,
                onSubmitScore = onSubmitScore
            )
            is GameState.Submitting -> SubmittingContent()
            is GameState.Submitted -> SubmittedContent(
                currentScore = currentScore,
                onNavigateToLeaderboard = onNavigateToLeaderboard,
                onRestart = onRestart
            )
            else -> {}
        }
    }
    }
}

@Composable
fun GameOverContent(
    currentScore: Int,
    highScore: Int,
    autoPlayCount: Int,
    gameState: GameState,
    onRestart: () -> Unit,
    onAutoplay: () -> Unit,
    onSubmitScore: () -> Unit
) {
    val isNewHighScore = currentScore > highScore && currentScore > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = ErrorRed.copy(alpha = 0.5f),
                ambientColor = ErrorRed.copy(alpha = 0.5f)
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF222230).copy(alpha = 0.98f),
                        Color(0xFF0F0F1A).copy(alpha = 0.98f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated Game Over title
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )

        Text(
            text = if (gameState is GameState.Ready) "NEON FLIP" else "GAME OVER",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (gameState is GameState.Ready) NeonCyan else ErrorRed,
            letterSpacing = 2.sp,
            modifier = Modifier.scale(pulseScale)
        )

        Spacer(Modifier.height(24.dp))

        // Score display with animation
        val scoreScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "score_appear"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scoreScale)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = NeonCyan.copy(alpha = 0.4f),
                    ambientColor = NeonCyan.copy(alpha = 0.2f)
                )
                .border(
                    width = 1.dp,
                    color = NeonCyan.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF13131C).copy(alpha = 0.8f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YOUR SCORE",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = currentScore.toString(),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )

                if (isNewHighScore) {
                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "NEW HIGH SCORE!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Action buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameActionButton(
                    text = if (gameState is GameState.Ready) "PLAY" else "RETRY",
                    icon = if (gameState is GameState.Ready) Icons.Default.PlayArrow else Icons.Default.Refresh,
                    onClick = onRestart,
                    color = NeonGreen,
                    modifier = Modifier.weight(1f)
                )

                if (gameState is GameState.GameOver) {
                    GameActionButton(
                        text = "SUBMIT",
                        icon = Icons.Default.Check,
                        onClick = onSubmitScore,
                        color = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SubmittingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = NeonCyan.copy(alpha = 0.4f),
                ambientColor = NeonCyan.copy(alpha = 0.2f)
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF222230).copy(alpha = 0.98f),
                        Color(0xFF0F0F1A).copy(alpha = 0.98f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = NeonCyan,
            strokeWidth = 4.dp
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Submitting Score...",
            fontSize = 18.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun SubmittedContent(
    currentScore: Int,
    onNavigateToLeaderboard: () -> Unit,
    onRestart: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "success")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = SuccessGreen.copy(alpha = 0.4f),
                ambientColor = SuccessGreen.copy(alpha = 0.2f)
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF222230).copy(alpha = 0.98f),
                        Color(0xFF0F0F1A).copy(alpha = 0.98f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success icon with glow
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SuccessGreen.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        radius = 80f
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .rotate(rotation)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "SCORE SUBMITTED!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SuccessGreen
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = currentScore.toString(),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )

        Spacer(Modifier.height(32.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GameActionButton(
                text = "HOME",
                icon = Icons.Default.Home,
                onClick = onNavigateToLeaderboard,
                color = NeonPurple,
                modifier = Modifier.weight(1f)
            )

            GameActionButton(
                text = "PLAY AGAIN",
                icon = Icons.Default.PlayArrow,
                onClick = onRestart,
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun GameActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_${text}"
    )

    val glowAlpha = if (isPressed) 0.8f else 0.4f
    val gradientColors = listOf(color, color.copy(alpha = 0.8f))

    Box(
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 4.dp else 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = color.copy(alpha = glowAlpha),
                ambientColor = color.copy(alpha = glowAlpha)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(Brush.horizontalGradient(gradientColors))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}
