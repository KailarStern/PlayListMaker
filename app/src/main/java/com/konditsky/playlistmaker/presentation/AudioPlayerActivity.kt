package com.konditsky.playlistmaker.presentation

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.konditsky.playlistmaker.R
import com.konditsky.playlistmaker.data.GlideImageLoader
import com.konditsky.playlistmaker.data.MediaPlayerManager
import com.konditsky.playlistmaker.domain.ImageLoader
import com.konditsky.playlistmaker.domain.Track
import com.konditsky.playlistmaker.domain.TrackPlayer
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var trackPlayer: TrackPlayer
    private lateinit var imageLoader: ImageLoader

    private lateinit var playButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var albumTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var albumCoverImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        initializeUIComponents()

        // Dependency injection or service locator can be used here
        trackPlayer = MediaPlayerManager()
        imageLoader = GlideImageLoader()

        val track = intent.getSerializableExtra("TRACK_DATA") as? Track
        track?.let {
            trackPlayer.prepare(it.previewUrl)
            updateTrackInfoUI(it)
        }

        playButton.setOnClickListener {
            if (trackPlayer.isPlaying()) {
                trackPlayer.pause()
                playButton.setImageResource(R.drawable.play_button)
            } else {
                trackPlayer.play()
                playButton.setImageResource(R.drawable.pause_button)
            }
        }
    }

    private fun initializeUIComponents() {
        playButton = findViewById(R.id.playButton)
        currentTimeTextView = findViewById(R.id.currentTime)
        trackNameTextView = findViewById(R.id.trackName)
        artistNameTextView = findViewById(R.id.artistName)
        albumTextView = findViewById(R.id.album)
        yearTextView = findViewById(R.id.year)
        genreTextView = findViewById(R.id.genre)
        countryTextView = findViewById(R.id.country)
        albumCoverImageView = findViewById(R.id.albumCover)
    }

    private fun updateTrackInfoUI(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        albumTextView.text = track.collectionName ?: getString(R.string.unknown_album)
        yearTextView.text = track.getFormattedReleaseYear()
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country

        imageLoader.loadImage(track.artworkUrl100, albumCoverImageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        trackPlayer.release()
    }
}