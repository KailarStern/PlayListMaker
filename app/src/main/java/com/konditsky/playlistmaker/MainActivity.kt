package com.konditsky.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    companion object {
        private const val THEME_PREF = "com.konditsky.playlistmaker.prefs"
        private const val DARK_THEME = "DARK_THEME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadThemeSetting()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1 = findViewById<Button>(R.id.buttonMainSearch)
        val button2 = findViewById<Button>(R.id.buttonMainMediateka)
        val button3 = findViewById<Button>(R.id.buttonMainSettings)

        button1.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadThemeSetting() {
        val prefs = getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean(DARK_THEME, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}