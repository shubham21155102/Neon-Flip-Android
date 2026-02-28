package com.neonflip.domain.usecase

import android.util.Log
import com.neonflip.core.common.Result
import com.neonflip.domain.model.Score
import com.neonflip.domain.repository.ScoreRepository
import javax.inject.Inject

/**
 * Use case for submitting a game score
 */
class SubmitScoreUseCase @Inject constructor(
    private val scoreRepository: ScoreRepository
) {
    suspend operator fun invoke(score: Int): Result<Score> {
        Log.d("SubmitScoreUseCase", "invoke called with score: $score")
        // Validate input
        if (score < 0) {
            Log.e("SubmitScoreUseCase", "Score is negative: $score")
            return Result.error(IllegalArgumentException("Score cannot be negative"), "Score cannot be negative")
        }

        Log.d("SubmitScoreUseCase", "Calling scoreRepository.submitScore")
        return scoreRepository.submitScore(score)
    }
}
