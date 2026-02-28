package com.neonflip.data.repository

import android.util.Log
import com.neonflip.core.common.Result
import com.neonflip.data.mapper.ErrorMapper
import com.neonflip.data.mapper.ScoreMapper
import com.neonflip.data.remote.api.ScoreApiService
import com.neonflip.data.remote.dto.SubmitScoreRequestDto
import com.neonflip.domain.model.Score
import com.neonflip.domain.repository.ScoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of ScoreRepository
 */
class ScoreRepositoryImpl @Inject constructor(
    private val scoreApiService: ScoreApiService
) : ScoreRepository {

    override suspend fun submitScore(score: Int): Result<Score> {
        Log.d("ScoreRepository", "submitScore called with score: $score")
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ScoreRepository", "Creating request")
                val request = SubmitScoreRequestDto(score)
                Log.d("ScoreRepository", "Calling API with request: $request")

                val response = scoreApiService.submitScore(request)
                Log.d("ScoreRepository", "Got response: code=${response.code()}, successful=${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    val score = ScoreMapper.toDomain(response.body()!!)
                    Log.d("ScoreRepository", "Success! Score ID: ${score.id}")
                    Result.success(score)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ScoreRepository", "Failed. Code: ${response.code()}, Body: $errorBody")
                    Result.error(Exception("HTTP ${response.code()}"), "Failed to submit score")
                }
            } catch (e: Exception) {
                Log.e("ScoreRepository", "Exception: ${e.javaClass.simpleName}: ${e.message}", e)
                Result.error(e, "Error: ${e.message}")
            }
        }
    }

    override suspend fun getLeaderboard(): Result<List<Score>> {
        return try {
            val response = scoreApiService.getLeaderboard()

            if (response.isSuccessful && response.body() != null) {
                val scores = response.body()!!.map { ScoreMapper.toDomain(it) }
                Result.success(scores)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val gson = com.google.gson.Gson()
                        val errorDto = gson.fromJson(errorBody, com.neonflip.data.remote.dto.ErrorResponseDto::class.java)
                        ErrorMapper.toDomain(errorDto, response.code()).message
                    } catch (e: Exception) {
                        "Failed to get leaderboard: ${response.message()}"
                    }
                } else {
                    "Failed to get leaderboard: ${response.message()}"
                }
                Result.error(Exception(errorMessage), errorMessage)
            }
        } catch (e: Exception) {
            Result.error(e, e.message)
        }
    }
}
