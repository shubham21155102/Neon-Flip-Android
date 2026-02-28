package com.neonflip.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.neonflip.domain.model.User
import com.neonflip.presentation.auth.AuthViewModel
import com.neonflip.presentation.game.GameScreen
import com.neonflip.presentation.leaderboard.LeaderboardScreen
import com.neonflip.presentation.login.LoginScreen
import com.neonflip.presentation.register.RegisterScreen
import androidx.compose.runtime.collectAsState

/**
 * Main navigation graph for the app
 */
@Composable
fun NeonFlipNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String = Route.START_ROUTE
) {
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(Route.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Route.Register.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onNavigateToGame = {
                    navController.navigate(Route.Game.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Route.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Register.route) { inclusive = true }
                    }
                },
                onNavigateToGame = {
                    navController.navigate(Route.Game.route) {
                        popUpTo(Route.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Game Screen - requires authentication
        composable(Route.Game.route) {
            val currentUser = when (val state = authState) {
                is com.neonflip.presentation.auth.AuthState.Authenticated -> state.user
                else -> null
            }

            // If not authenticated, navigate to login
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Game.route) { inclusive = true }
                    }
                }
            } else {
                GameScreen(
                    user = currentUser,
                    onNavigateToLeaderboard = {
                        navController.navigate(Route.Leaderboard.route)
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Game.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Leaderboard Screen
        composable(Route.Leaderboard.route) {
            LeaderboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
