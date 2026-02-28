package com.neonflip.presentation.leaderboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neonflip.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Professional Animated Leaderboard Screen
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val leaderboardState by viewModel.leaderboardState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Start loading when screen appears
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bg_gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LEADERBOARD",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = NeonCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonPink,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    val refreshTransition = rememberInfiniteTransition(label = "refresh_rotation")
                    val rotation by refreshTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "refresh"
                    )

                    IconButton(
                        onClick = {
                            isRefreshing = true
                            viewModel.refresh()
                            coroutineScope.launch {
                                delay(1000)
                                isRefreshing = false
                            }
                        },
                        enabled = !isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = if (isRefreshing) TextSecondary else NeonGreen,
                            modifier = Modifier
                                .size(24.dp)
                                .then(
                                    if (isRefreshing) {
                                        Modifier.rotate(rotation)
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0A0A1F),
                            Color(0xFF1A0A2E),
                            Color(0xFF0A0A1F)
                        ),
                        start = Offset(0f, gradientOffset),
                        end = Offset(0f, gradientOffset + 1000f)
                    )
                )
        ) {
            // Animated background particles
            AnimatedBackgroundParticles()

            when (val state = leaderboardState) {
                is LeaderboardState.Loading -> {
                    LoadingContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
                is LeaderboardState.Success -> {
                    if (state.scores.isEmpty()) {
                        EmptyStateContent(
                            onRefresh = { viewModel.refresh() },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    } else {
                        LeaderboardContent(
                            scores = state.scores,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    }
                }
                is LeaderboardState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRefresh = { viewModel.refresh() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedBackgroundParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particles_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        // Floating orbs
        val orb1 = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            infiniteRepeatable(
                animation = tween(4000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orb1"
        ).value

        val orb2 = infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            infiniteRepeatable(
                animation = tween(5000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orb2"
        ).value

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        CircleShape
                    )
                    .alpha(orb1)
            )
        }

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomStart)
                .padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NeonPink.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        CircleShape
                    )
                    .alpha(orb2)
            )
        }
    }
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_rotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .rotate(rotation),
                color = NeonCyan,
                strokeWidth = 4.dp
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Loading Leaderboard...",
                fontSize = 18.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun LeaderboardContent(
    scores: List<com.neonflip.domain.model.Score>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header with title and stats
        LeaderboardHeader(
            scoreCount = scores.size,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Scores list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = scores,
                key = { it.id }
            ) { score ->
                AnimatedScoreItem(
                    rank = scores.indexOf(score) + 1,
                    score = score
                )
            }
        }
    }
}

@Composable
fun LeaderboardHeader(
    scoreCount: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "header_scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceVariant.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "TOP $scoreCount",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }

            Text(
                text = "PLAYERS",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedScoreItem(
    rank: Int,
    score: com.neonflip.domain.model.Score
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "item_$rank"
    )

    val slideOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = tween(
            durationMillis = 300 + (rank * 50), // Stagger animations
            easing = EaseOutCubic
        ),
        label = "slide_$rank"
    )

    // Card design based on rank
    data class RankStyle(
        val backgroundColor: Color,
        val borderColor: Color,
        val rankColor: Color,
        val icon: androidx.compose.ui.graphics.vector.ImageVector?
    )

    val rankStyle = when (rank) {
        1 -> RankStyle(
            Color(0xFFFFD700).copy(alpha = 0.15f), // Gold background
            Color(0xFFFFD700), // Gold border
            Color(0xFFFFD700), // Gold text
            Icons.Default.Star
        )
        2 -> RankStyle(
            Color(0xFFC0C0C0).copy(alpha = 0.15f), // Silver background
            Color(0xFFC0C0C0), // Silver border
            Color(0xFFC0C0C0), // Silver text
            Icons.Default.Star
        )
        3 -> RankStyle(
            Color(0xFFCD7F32).copy(alpha = 0.15f), // Bronze background
            Color(0xFFCD7F32), // Bronze border
            Color(0xFFCD7F32), // Bronze text
            Icons.Default.Star
        )
        else -> RankStyle(
            Color.Transparent,
            Color.Transparent,
            TextSecondary,
            null
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = slideOffset.dp)
            .scale(scale)
            .then(
                if (rankStyle.backgroundColor != Color.Transparent) {
                    Modifier.border(
                        2.dp,
                        rankStyle.borderColor.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                        .border(
                            1.dp,
                            TextTertiary.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        )
                }
            )
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (rankStyle.backgroundColor == Color.Transparent) {
                Color(0xFF1A1A24).copy(alpha = 0.8f)
            } else {
                rankStyle.backgroundColor
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            RankBadge(
                rank = rank,
                color = rankStyle.rankColor,
                hasIcon = rankStyle.icon != null
            )

            // Username
            Text(
                text = score.username,
                modifier = Modifier.weight(2.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            // Score
            Text(
                text = score.score.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NeonGreen
            )
        }
    }
}

@Composable
fun RankBadge(
    rank: Int,
    color: Color,
    hasIcon: Boolean
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .then(
                if (hasIcon) {
                    Modifier.background(
                        Brush.radialGradient(
                            colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                            radius = 40f
                        ),
                        CircleShape
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasIcon && rank <= 3) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = "#$rank",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun EmptyStateContent(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val scale by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0.8f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "empty_scale"
        )

        Column(
            modifier = Modifier.scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier
                        .size(48.dp)
                        .alpha(0.5f)
                )
            }

            Text(
                text = "No scores yet!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Be the first to set a score",
                fontSize = 16.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(32.dp))

            ModernOutlineButton(
                text = "START PLAYING",
                icon = Icons.Default.PlayArrow,
                onClick = { /* Navigate to game */ },
                color = NeonCyan
            )
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ErrorRed.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Oops!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ErrorRed
        )

        Text(
            text = message,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        ModernOutlineButton(
            text = "TRY AGAIN",
            icon = Icons.Default.Refresh,
            onClick = onRefresh,
            color = NeonCyan
        )
    }
}

@Composable
fun ModernOutlineButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .border(2.dp, color, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.sp
        )
    }
}
