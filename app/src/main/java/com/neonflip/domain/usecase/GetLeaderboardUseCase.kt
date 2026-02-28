package com.neonflip.domain.usecase

import com.neonflip.core.common.Result
import com.neonflip.domain.model.Score
import com.neonflip.domain.repository.ScoreRepository
import javax.inject.Inject

/**
 * Use case for getting the leaderboard
 */
class GetLeaderboardUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    suspend operator fun invoke(): Result<List<Score>> {
        return scoreRepository.getLeaderboard()
    }
}
