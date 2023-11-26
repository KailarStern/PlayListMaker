package com.konditsky.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val track = intent.getSerializableExtra("TRACK_DATA") as? Track

        val backButton = findViewById<ImageView>(R.id.backIcon)
        backButton.setOnClickListener {
            onBackPressed()
        }

        track?.let {
            findViewById<TextView>(R.id.trackName).text = it.trackName
            findViewById<TextView>(R.id.artistName).text = it.artistName
            findViewById<TextView>(R.id.album).text = it.collectionName ?: "Unknown Album"
            findViewById<TextView>(R.id.year).text = it.getFormattedReleaseYear()
            findViewById<TextView>(R.id.genre).text = it.primaryGenreName
            findViewById<TextView>(R.id.country).text = it.country

            val albumCoverImageView = findViewById<ImageView>(R.id.albumCover)
            Glide.with(this)
                .load(it.getHighQualityArtworkUrl())
                .into(albumCoverImageView)
        }
    }
}
