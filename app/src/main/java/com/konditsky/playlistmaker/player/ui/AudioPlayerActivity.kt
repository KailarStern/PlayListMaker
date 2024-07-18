package com.konditsky.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.konditsky.playlistmaker.R
import com.konditsky.playlistmaker.search.ui.Track

class AudioPlayerActivity : AppCompatActivity() {
    private val viewModel: AudioPlayerViewModel by viewModels()
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

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            viewModel.updateCurrentPosition()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        initializeUIComponents()
        setupObservers()
        handler.post(updateRunnable)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val track = intent.getSerializableExtra("TRACK_DATA") as? Track
        track?.let {
            updateTrackInfoUI(it)
        }

        playButton.setOnClickListener {
            track?.let {
                viewModel.playOrPause(it.previewUrl)
            }
        }
    }

    private fun initializeUIComponents() {
        imageLoader = GlideImageLoader()
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

    private fun setupObservers() {
        viewModel.playbackState.observe(this, Observer { isPlaying ->
            if (isPlaying) {
                playButton.setImageResource(R.drawable.pause_button)
            } else {
                playButton.setImageResource(R.drawable.play_button)
            }
        })

        viewModel.currentPosition.observe(this, Observer { position ->
            currentTimeTextView.text = formatTime(position)
        })

        viewModel.isTrackCompleted.observe(this, Observer { isCompleted ->
            if (isCompleted) {
                playButton.setImageResource(R.drawable.play_button)
                currentTimeTextView.text = formatTime(0)
            }
        })
    }

    private fun updateTrackInfoUI(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        albumTextView.text = track.collectionName ?: getString(R.string.unknown_album)
        yearTextView.text = track.getFormattedReleaseYear()
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        imageLoader.loadImage(track.getHighQualityArtworkUrl(), albumCoverImageView)
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }
}






