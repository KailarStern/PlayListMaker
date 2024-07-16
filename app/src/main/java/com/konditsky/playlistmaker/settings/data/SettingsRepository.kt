package com.konditsky.playlistmaker.settings.data


import com.konditsky.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isDarkTheme: Boolean)
}