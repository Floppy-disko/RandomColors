package com.veo.randomcolors

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "color_preferences")

class ColorPreferencesRepository(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    private object PreferenceKeys {
        val IS_RGB_SPECTRUM_MODE = booleanPreferencesKey("is_rgb_spectrum_mode")
        val COLOR_SET_HEX = stringPreferencesKey("color_set_hex")
        val CURRENT_COLOR_HEX = stringPreferencesKey("current_color_hex")
    }

    val isRgbSpectrumMode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.IS_RGB_SPECTRUM_MODE] ?: false
        }

    val colorSet: Flow<List<Color>> = dataStore.data
        .map { preferences ->
            val raw = preferences[PreferenceKeys.COLOR_SET_HEX]
            if (raw == null) {
                DEFAULT_COLORS
            } else if (raw.isEmpty()) {
                emptyList()
            } else {
                raw.split(",")
                    .mapNotNull { hexToColor(it) }
            }
        }

    val currentColor: Flow<Color?> = dataStore.data
        .map { preferences ->
            val raw = preferences[PreferenceKeys.CURRENT_COLOR_HEX]
            if (!raw.isNullOrEmpty()) hexToColor(raw) else null
        }

    suspend fun setRgbSpectrumMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_RGB_SPECTRUM_MODE] = enabled
        }
    }

    suspend fun saveColorSet(colors: List<Color>) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.COLOR_SET_HEX] = colors.joinToString(",") { it.toHex() }
        }
    }

    suspend fun saveCurrentColor(color: Color?) {
        dataStore.edit { preferences ->
            if (color != null) {
                preferences[PreferenceKeys.CURRENT_COLOR_HEX] = color.toHex()
            } else {
                preferences.remove(PreferenceKeys.CURRENT_COLOR_HEX)
            }
        }
    }
}
