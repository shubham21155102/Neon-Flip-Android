package com.neonflip.domain.repository

import com.neonflip.core.common.Result
import com.neonflip.domain.model.Score

/**
 * Repository interface for Score operations
 */
interface ScoreRepository {
    /**
     * Submit a score
     */
    suspend fun submitScore(score: Int): Result<Score>

    /**
     * Get the leaderboard
     */
    suspend fun getLeaderboard(): Result<List<Score>>
}
