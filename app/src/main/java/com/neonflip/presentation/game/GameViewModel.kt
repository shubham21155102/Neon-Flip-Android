package com.neonflip.presentation.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neonflip.core.common.Result
import com.neonflip.domain.model.User
import com.neonflip.domain.usecase.SubmitScoreUseCase
import com.neonflip.data.local.SettingsStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Game Screen
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val submitScoreUseCase: SubmitScoreUseCase,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState.Ready)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore.asStateFlow()

    // Read the auto play count from DataStore. Defaults to 0 while loading.
    val autoPlayCount: StateFlow<Int> = settingsStorage.autoPlayCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _isAutoPlayEnabled = MutableStateFlow(false)
    val isAutoPlayEnabled: StateFlow<Boolean> = _isAutoPlayEnabled.asStateFlow()

    fun startGame(isAutoPlay: Boolean = false) {
        _gameState.value = GameState.Playing
        _currentScore.value = 0
        _isAutoPlayEnabled.value = isAutoPlay

        if (isAutoPlay) {
            viewModelScope.launch {
                settingsStorage.incrementAutoPlayCount()
            }
        }
    }

    fun updateScore(score: Int) {
        _currentScore.value = score
    }

    fun gameOver() {
        _gameState.value = GameState.GameOver
        // Check if new high score
        if (_currentScore.value > _highScore.value) {
            _highScore.value = _currentScore.value
        }
    }

    /**
     * Set the current user (called when user is authenticated)
     */
    fun setUser(user: User?) {
        // User is now passed via GameScreen, keeping for compatibility
    }

    /**
     * Submit score - user is always authenticated since login is required
     */
    fun submitScore() {
        Log.d("GameViewModel", "submitScore called with score: ${_currentScore.value}")

        viewModelScope.launch {
            try {
                _gameState.value = GameState.Submitting
                Log.d("GameViewModel", "Calling submitScoreUseCase with score: ${_currentScore.value}")
                val result = submitScoreUseCase(_currentScore.value)
                Log.d("GameViewModel", "submitScoreUseCase result: $result")
                when (result) {
                    is Result.Success -> {
                        Log.d("GameViewModel", "Score submitted successfully")
                        _gameState.value = GameState.Submitted
                    }
                    is Result.Error -> {
                        Log.e("GameViewModel", "Failed to submit score: ${result.message}")
                        _gameState.value = GameState.Error("Failed to submit score")
                    }
                    is Result.Loading -> {
                        _gameState.value = GameState.Submitting
                    }
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Exception in submitScore: ${e.message}", e)
                _gameState.value = GameState.Error("Error: ${e.message}")
            }
        }
    }

    fun restart() {
        _gameState.value = GameState.Ready
        _currentScore.value = 0
        _isAutoPlayEnabled.value = false
    }
}

sealed class GameState {
    data object Ready : GameState()
    data object Playing : GameState()
    data object GameOver : GameState()
    data object Submitting : GameState()
    data object Submitted : GameState()
    data class Error(val message: String) : GameState()
}
