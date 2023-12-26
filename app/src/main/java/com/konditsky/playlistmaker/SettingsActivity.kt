package com.konditsky.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.buttonSettingsBack)
        val shareImageView = findViewById<ImageView>(R.id.imageSettingsShare)
        val supportImageView = findViewById<ImageView>(R.id.imageSettingsSupport)
        val termsImageView = findViewById<ImageView>(R.id.imageSettingsTerms)
        val themeSwitch = findViewById<Switch>(R.id.switchSettingsTheme)

        themeSwitch.isChecked = loadThemeSetting()

        backButton.setOnClickListener {
            finish()
        }

        shareImageView.setOnClickListener {
            shareAppLink()
        }

        supportImageView.setOnClickListener {
            sendSupportEmail()
        }

        termsImageView.setOnClickListener {
            openTermsInBrowser()
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveThemeSetting(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun shareAppLink() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.yandexLinkDeveloper))
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    private fun sendSupportEmail() {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.text_to_dv1st))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.text_to_dv2st))
            startActivity(this)
        }
    }

    private fun openTermsInBrowser() {
        val termsUrl = getString(R.string.yandexLinkOffer)
        val termsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsUrl)
        }
        startActivity(termsIntent)
    }

    private fun saveThemeSetting(isDarkTheme: Boolean) {
        val prefs = getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE).edit()
        prefs.putBoolean(DARK_THEME, isDarkTheme)
        prefs.apply()
    }

    private fun loadThemeSetting(): Boolean {
        val prefs = getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE)
        return prefs.getBoolean(DARK_THEME, false)
    }

    companion object {
        private const val THEME_PREF = "com.konditsky.playlistmaker.prefs"
        private const val DARK_THEME = "DARK_THEME"
        private const val EMAIL_ADDRESS = "SternKailar@yandex.ru"
    }

}







