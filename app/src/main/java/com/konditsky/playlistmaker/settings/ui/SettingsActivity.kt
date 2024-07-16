package com.konditsky.playlistmaker.settings.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.konditsky.playlistmaker.R
import com.konditsky.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.konditsky.playlistmaker.settings.domain.impl.SettingsInteractorImpl

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsInteractorImpl(SettingsRepositoryImpl(getSharedPreferences(THEME_PREF, Context.MODE_PRIVATE))))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.buttonSettingsBack)
        val shareImageView = findViewById<ImageView>(R.id.imageSettingsShare)
        val supportImageView = findViewById<ImageView>(R.id.imageSettingsSupport)
        val termsImageView = findViewById<ImageView>(R.id.imageSettingsTerms)
        val themeSwitch = findViewById<Switch>(R.id.switchSettingsTheme)

        viewModel.themeSettings.observe(this, Observer { settings ->
            themeSwitch.isChecked = settings.isDarkTheme
            AppCompatDelegate.setDefaultNightMode(
                if (settings.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        })


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
            viewModel.updateThemeSetting(isChecked)
        }

        viewModel.fetchThemeSettings()
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





    companion object {
        private const val THEME_PREF = "com.konditsky.playlistmaker.prefs"
        private const val EMAIL_ADDRESS = "SternKailar@yandex.ru"
    }

}







