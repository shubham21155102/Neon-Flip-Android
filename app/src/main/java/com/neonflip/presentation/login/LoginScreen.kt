package com.neonflip.presentation.login

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neonflip.presentation.auth.AuthViewModel
import com.neonflip.ui.theme.*

/**
 * Professional Animated Login Screen
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    var isVisible by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    // Show error message
    LaunchedEffect((loginState as? LoginState.Error)?.message) {
        if (loginState is LoginState.Error) {
            Toast.makeText(context, (loginState as LoginState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    // Handle login success - refresh auth and wait for authentication
    LaunchedEffect(loginState is LoginState.Success) {
        if (loginState is LoginState.Success) {
            authViewModel.refreshAuthState()
        }
    }

    // Navigate when authenticated
    LaunchedEffect(authState is com.neonflip.presentation.auth.AuthState.Authenticated) {
        if (authState is com.neonflip.presentation.auth.AuthState.Authenticated) {
            onNavigateToGame()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A0A1F),
                        Color(0xFF1A0A2E),
                        Color(0xFF0A0A1F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with animation
            val logoScale by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0.5f,
                animationSpec = tween(800, delayMillis = 0, easing = EaseOutBack),
                label = "logo"
            )
            val logoRotation by animateFloatAsState(
                targetValue = if (isVisible) 0f else -360f,
                animationSpec = tween(1000, delayMillis = 0, easing = EaseOutCubic),
                label = "logo_rotation"
            )

            Column(
                modifier = Modifier
                    .scale(logoScale)
                    .graphicsLayer { rotationZ = logoRotation },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Neon Logo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    NeonCyan.copy(alpha = 0.2f),
                                    NeonPink.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                radius = 100f
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer ring
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        NeonCyan.copy(alpha = 0.15f)
                                    ),
                                    radius = 80f
                                ),
                                CircleShape
                            )
                            .border(
                                2.dp,
                                Brush.sweepGradient(
                                    colors = listOf(
                                        NeonCyan,
                                        NeonPink,
                                        NeonPurple,
                                        NeonCyan
                                    )
                                ),
                                CircleShape
                            )
                    )

                    // Center icon - N with F style
                    Text(
                        text = "NF",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Glow effect under logo
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    NeonCyan,
                                    NeonPink,
                                    NeonCyan,
                                    Color.Transparent
                                )
                            ),
                            RoundedCornerShape(2.dp)
                        )
                )
            }

            Spacer(Modifier.height(24.dp))

            // Title with animation
            val titleScale by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0.8f,
                animationSpec = tween(800, delayMillis = 200, easing = EaseOutBack),
                label = "title"
            )

            Column(
                modifier = Modifier
                    .scale(titleScale),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NEON FLIP",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                    color = NeonCyan
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Gravity Defying Adventure",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFB0B0CC)
                )
            }

            Spacer(Modifier.height(48.dp))

            // Username field
            ModernTextField(
                value = username,
                onValueChange = viewModel::onUsernameChange,
                label = "Username",
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is LoginState.Loading
            )

            Spacer(Modifier.height(16.dp))

            // Password field
            ModernTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is LoginState.Loading
            )

            Spacer(Modifier.height(8.dp))

            // Error message
            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Login Button
            ModernLoginButton(
                onClick = { viewModel.login() },
                isLoading = loginState is LoginState.Loading,
                isEnabled = loginState !is LoginState.Loading &&
                        username.isNotBlank() &&
                        password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Register link
            TextButton(
                onClick = onNavigateToRegister,
                enabled = loginState !is LoginState.Loading
            ) {
                Text("Don't have an account? ", color = Color(0xFFB0B0CC))
                Text(" Register", color = NeonPink, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    val isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NeonCyan else Color(0xFF808099),
        tween(300, easing = EaseOutCubic),
        label = "border"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFFB0B0CC)) },
        singleLine = true,
        modifier = modifier
            .border(1.dp, borderColor, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large),
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0x33FFFFFF),
            unfocusedContainerColor = Color(0x33FFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = NeonCyan
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = if (isPassword) ImeAction.Done else ImeAction.Next
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
        shape = MaterialTheme.shapes.large
    )
}

@Composable
fun ModernLoginButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isLoading -> 0.97f
            isPressed -> 0.95f
            !isEnabled -> 0.9f
            else -> 1f
        },
        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )

    val gradientColors = when {
        !isEnabled -> listOf(NeonCyan.copy(alpha = 0.3f), NeonBlue.copy(alpha = 0.3f))
        else -> listOf(NeonCyan, NeonBlue)
    }

    val infiniteTransition = rememberInfiniteTransition("shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 500f,
        infiniteRepeatable(animation = tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { if (isEnabled && !isLoading) onClick() }
            )
            .background(
                Brush.linearGradient(gradientColors)
            )
            .then(
                if (isEnabled)
                    Modifier.border(
                        2.dp,
                        Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(16.dp)
                    )
                else
                    Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shimmer overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0f)
                        ),
                        start = Offset(shimmerOffset, 0f),
                        end = Offset(shimmerOffset + 500f, 0f)
                    )
                )
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = "LOGIN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 3.sp
                )
            }
        }

        // Glow effect
        if (isEnabled && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            radius = 100f
                        )
                    )
            )
        }
    }
}
