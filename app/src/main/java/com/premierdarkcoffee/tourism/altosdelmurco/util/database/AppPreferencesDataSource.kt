package com.premierdarkcoffee.tourism.altosdelmurco.util.database

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di.IoDispatcher
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.appPreferencesDataStore by preferencesDataStore(name = "app_preferences")

@Singleton
class AppPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    val themeMode: Flow<ThemeMode> = context.appPreferencesDataStore.data.map { preferences ->
        ThemeMode.valueOf(
            preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name,
        )
    }

    suspend fun setThemeMode(themeMode: ThemeMode) = withContext(ioDispatcher) {
        context.appPreferencesDataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.name
        }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
