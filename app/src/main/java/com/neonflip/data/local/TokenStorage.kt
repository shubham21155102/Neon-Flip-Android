package com.neonflip.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension to create DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/**
 * Storage for JWT tokens using DataStore
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    /**
     * Save the JWT token
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN_KEY] = token
        }
    }

    /**
     * Get the JWT token as Flow
     */
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.TOKEN_KEY]
        }
    }

    /**
     * Get the JWT token synchronously (returns null if not set)
     */
    suspend fun getTokenSync(): String? {
        var token: String? = null
        context.dataStore.data.collect { preferences ->
            token = preferences[PreferencesKeys.TOKEN_KEY]
        }
        return token
    }

    /**
     * Save the user ID
     */
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID_KEY] = userId
        }
    }

    /**
     * Get the user ID as Flow
     */
    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID_KEY]
        }
    }

    /**
     * Check if user is logged in (has token)
     */
    fun isLoggedIn(): Flow<Boolean> {
        return getToken().map { it != null }
    }

    /**
     * Clear all stored data (logout)
     */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
