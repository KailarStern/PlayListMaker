package com.konditsky.playlistmaker.settings.domain

import com.konditsky.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isDarkTheme: Boolean)
}