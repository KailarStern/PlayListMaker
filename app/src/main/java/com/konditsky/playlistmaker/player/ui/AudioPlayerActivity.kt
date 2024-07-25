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
import com.konditsky.playlistmaker.player.data.TrackPlayerRepositoryImpl
import com.konditsky.playlistmaker.player.domain.TrackPlayerInteractor
import com.konditsky.playlistmaker.search.ui.Track

class AudioPlayerActivity : AppCompatActivity() {
    private val viewModel: AudioPlayerViewModel by viewModels {
        AudioPlayerViewModelFactory(TrackPlayerInteractor(TrackPlayerRepositoryImpl()))
    }

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var playButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var albumTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var albumCoverImageView: ImageView
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                viewModel.updateCurrentPosition()
                handler.postDelayed(this, 1000)
            }
        }

        initializeUIComponents()
        setupObservers()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val track = intent.getSerializableExtra("TRACK_DATA") as? Track
        track?.let {
            viewModel.prepare(it.previewUrl)
            updateTrackInfoUI(it)
        }

        playButton.setOnClickListener {
            viewModel.playOrPause()
            if (viewModel.screenState.value?.isPlaying == true) {
                handler.post(runnable)
            } else {
                handler.removeCallbacks(runnable)
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
        viewModel.screenState.observe(this, Observer { state ->
            state?.let {
                playButton.setImageResource(if (it.isPlaying) R.drawable.pause_button else R.drawable.play_button)
                currentTimeTextView.text = formatTime(it.currentPosition)
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
        handler.removeCallbacks(runnable)
    }
}




































