package com.konditsky.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.buttonSettingsBack)
        val shareImageView = findViewById<ImageView>(R.id.imageSettingsShare)
        val supportImageView = findViewById<ImageView>(R.id.imageSettingsSupport)
        val termsImageView = findViewById<ImageView>(R.id.imageSettingsTerms)

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
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("SternKailar@yandex.ru"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.text_to_dv1st))
            putExtra(Intent.EXTRA_TEXT,getString(R.string.text_to_dv2st))
        }
        startActivity(emailIntent)
    }

    private fun openTermsInBrowser() {
        val termsUrl = getString(R.string.yandexLinkOffer)
        val termsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsUrl)
        }
        startActivity(termsIntent)
    }


}



