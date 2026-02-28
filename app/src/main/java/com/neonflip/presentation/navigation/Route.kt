package com.neonflip.presentation.navigation

/**
 * Sealed class defining all navigation routes in the app
 */
sealed class Route(val route: String) {
    data object Login : Route("login")
    data object Register : Route("register")
    data object Game : Route("game")
    data object Leaderboard : Route("leaderboard")

    companion object {
        // Starting route
        const val START_ROUTE = "login"
    }
}
