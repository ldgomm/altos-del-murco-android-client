package com.premierdarkcoffee.tourism.altosdelmurco.util.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.AppPreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

data class AppThemeUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {

    val uiState: StateFlow<AppThemeUiState> = appPreferencesDataSource.themeMode
        .map(::AppThemeUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppThemeUiState(),
        )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            appPreferencesDataSource.setThemeMode(themeMode)
        }
    }
}