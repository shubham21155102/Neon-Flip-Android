package com.neonflip.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension to create DataStore
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "game_settings")

/**
 * Storage for game settings using DataStore.
 * Used to persist data like the auto-play usage count.
 */
@Singleton
class SettingsStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val AUTO_PLAY_KEY = intPreferencesKey("auto_play_count")
        const val MAX_AUTO_PLAYS = 3
    }

    /**
     * Flow of the current auto play count. Defaults to 0 if not set.
     */
    val autoPlayCount: Flow<Int> = context.settingsDataStore.data.map { preferences ->
        preferences[AUTO_PLAY_KEY] ?: 0
    }

    /**
     * Increments the auto play count by 1.
     */
    suspend fun incrementAutoPlayCount() {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[AUTO_PLAY_KEY] ?: 0
            if (current < MAX_AUTO_PLAYS) {
                preferences[AUTO_PLAY_KEY] = current + 1
            }
        }
    }
}
