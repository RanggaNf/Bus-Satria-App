package com.bussatriaappdriver.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface SettingsRepository {
    val isDarkThemeFlow: Flow<Boolean>
    val isLocationPermissionGrantedFlow: Flow<Boolean>
    val isNotificationEnabledFlow: Flow<Boolean>

    suspend fun toggleDarkTheme()
    suspend fun toggleLocationPermission()
    suspend fun toggleNotificationPermission()
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val dataStore: DataStore<Preferences> = context.dataStore

    override val isDarkThemeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] ?: false
        }

    override val isLocationPermissionGrantedFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOCATION_PERMISSION_GRANTED] ?: false
        }

    override val isNotificationEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_NOTIFICATION_ENABLED] ?: false
        }

    override suspend fun toggleDarkTheme() {
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.IS_DARK_THEME] ?: false
            preferences[PreferencesKeys.IS_DARK_THEME] = !current
        }
    }

    override suspend fun toggleLocationPermission() {
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.IS_LOCATION_PERMISSION_GRANTED] ?: false
            preferences[PreferencesKeys.IS_LOCATION_PERMISSION_GRANTED] = !current
        }
    }

    override suspend fun toggleNotificationPermission() {
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.IS_NOTIFICATION_ENABLED] ?: false
            preferences[PreferencesKeys.IS_NOTIFICATION_ENABLED] = !current
        }
    }

    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val IS_LOCATION_PERMISSION_GRANTED = booleanPreferencesKey("is_location_permission_granted")
        val IS_NOTIFICATION_ENABLED = booleanPreferencesKey("is_notification_enabled")
    }
}