package com.konditsky.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val track = intent.getSerializableExtra("TRACK_DATA") as? Track

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
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
            val radiusDp = 8
            val density = resources.displayMetrics.density
            val radiusPx = (radiusDp * density).toInt()

            val artworkUrl = it.getHighQualityArtworkUrl()
            val placeholderResId = R.drawable.ic_mock_task

            Glide.with(this)
                .load(artworkUrl)
                .error(Glide.with(this).load(placeholderResId))
                .transform(RoundedCorners(radiusPx))
                .into(albumCoverImageView)
        }
    }
}
