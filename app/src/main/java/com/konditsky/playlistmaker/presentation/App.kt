package com.konditsky.playlistmaker.presentation

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        loadThemeSetting()
    }

    private fun loadThemeSetting() {
        val prefs = getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean(DARK_THEME, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        private const val THEME_PREF = "com.konditsky.playlistmaker.prefs"
        private const val DARK_THEME = "DARK_THEME"
    }
}
