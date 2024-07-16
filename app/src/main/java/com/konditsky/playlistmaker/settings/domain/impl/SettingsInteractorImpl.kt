package com.konditsky.playlistmaker.settings.domain.impl

import com.konditsky.playlistmaker.settings.data.SettingsRepository
import com.konditsky.playlistmaker.settings.domain.SettingsInteractor
import com.konditsky.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(isDarkTheme: Boolean) {
        settingsRepository.updateThemeSetting(isDarkTheme)
    }
}