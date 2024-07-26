package com.konditsky.playlistmaker.main.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.konditsky.playlistmaker.R
import com.konditsky.playlistmaker.sharing.ui.MediaActivity
import com.konditsky.playlistmaker.search.ui.SearchActivity
import com.konditsky.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
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

}