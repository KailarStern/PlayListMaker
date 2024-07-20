package com.konditsky.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konditsky.playlistmaker.settings.domain.SettingsInteractor
import com.konditsky.playlistmaker.settings.domain.model.ThemeSettings
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {
    private val _themeSettings = MutableLiveData<ThemeSettings>()
    val themeSettings: LiveData<ThemeSettings> get() = _themeSettings

    fun fetchThemeSettings() {
        viewModelScope.launch {
            val settings = settingsInteractor.getThemeSettings()
            _themeSettings.value = settings
        }
    }

    fun updateThemeSetting(isDarkTheme: Boolean) {
        viewModelScope.launch {
            settingsInteractor.updateThemeSetting(isDarkTheme)
            _themeSettings.value = ThemeSettings(isDarkTheme)
        }
    }
}

