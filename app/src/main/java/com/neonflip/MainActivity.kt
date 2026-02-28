package com.neonflip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.neonflip.data.local.TokenStorage
import com.neonflip.presentation.auth.AuthViewModel
import com.neonflip.presentation.navigation.NeonFlipNavGraph
import com.neonflip.presentation.navigation.Route
import com.neonflip.ui.theme.NeonFlipTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeonFlipTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    var startDestination by remember { mutableStateOf(Route.Login.route) }

                    // Check login state on app startup
                    LaunchedEffect(Unit) {
                        val token = tokenStorage.getToken().first()
                        startDestination = if (token != null) Route.Game.route else Route.Login.route
                    }

                    NeonFlipNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
