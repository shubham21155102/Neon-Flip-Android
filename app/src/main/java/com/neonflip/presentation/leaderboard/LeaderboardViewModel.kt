package com.neonflip.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neonflip.core.common.Result
import com.neonflip.domain.model.Score
import com.neonflip.domain.usecase.GetLeaderboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Leaderboard Screen
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getLeaderboardUseCase: GetLeaderboardUseCase
) : ViewModel() {

    private val _leaderboardState = MutableStateFlow<LeaderboardState>(LeaderboardState.Loading)
    val leaderboardState: StateFlow<LeaderboardState> = _leaderboardState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _leaderboardState.value = LeaderboardState.Loading
            when (val result = getLeaderboardUseCase()) {
                is Result.Success -> {
                    _leaderboardState.value = LeaderboardState.Success(result.data)
                }
                is Result.Error -> {
                    _leaderboardState.value = LeaderboardState.Error(
                        result.message ?: "Failed to load leaderboard"
                    )
                }
                is Result.Loading -> {
                    _leaderboardState.value = LeaderboardState.Loading
                }
            }
        }
    }

    fun refresh() {
        loadLeaderboard()
    }
}

sealed class LeaderboardState {
    data object Loading : LeaderboardState()
    data class Success(val scores: List<Score>) : LeaderboardState()
    data class Error(val message: String) : LeaderboardState()
}
