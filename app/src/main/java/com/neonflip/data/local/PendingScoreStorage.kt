package com.neonflip.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Storage for pending scores that need to be submitted after login
 */
@Singleton
class PendingScoreStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PENDING_SCORE_PREFS,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PENDING_SCORE_PREFS = "pending_scores_prefs"
        private const val KEY_PENDING_SCORE = "pending_score"
        private const val KEY_HAS_PENDING_SCORE = "has_pending_score"
    }

    /**
     * Save a score that will be submitted after login
     */
    fun savePendingScore(score: Int) {
        prefs.edit().apply {
            putInt(KEY_PENDING_SCORE, score)
            putBoolean(KEY_HAS_PENDING_SCORE, true)
            apply()
        }
    }

    /**
     * Get the pending score
     */
    fun getPendingScore(): Int? {
        return if (hasPendingScore()) {
            prefs.getInt(KEY_PENDING_SCORE, 0)
        } else {
            null
        }
    }

    /**
     * Check if there's a pending score
     */
    fun hasPendingScore(): Boolean {
        return prefs.getBoolean(KEY_HAS_PENDING_SCORE, false)
    }

    /**
     * Clear the pending score after submitting
     */
    fun clearPendingScore() {
        prefs.edit().apply {
            remove(KEY_PENDING_SCORE)
            remove(KEY_HAS_PENDING_SCORE)
            apply()
        }
    }
}
