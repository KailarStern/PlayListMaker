package com.konditsky.playlistmaker

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class AudioPlayerActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
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

        val track = intent.getSerializableExtra("TRACK_DATA") as? Track
        track?.let {
            setupMediaPlayer(it.previewUrl)
            updateTrackInfoUI(it)
        }

        playButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                playButton.setImageResource(R.drawable.play_button)
            } else {
                mediaPlayer?.start()
                playButton.setImageResource(R.drawable.pause_button)
                updateProgress()
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

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateTrackInfoUI(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        albumTextView.text = track.collectionName ?: "Unknown Album"
        yearTextView.text = track.getFormattedReleaseYear()
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country

        val radiusDp = 8
        val density = resources.displayMetrics.density
        val radiusPx = (radiusDp * density).toInt()
        Glide.with(this)
            .load(track.getHighQualityArtworkUrl())
            .error(R.drawable.ic_mock_task)
            .transform(RoundedCorners(radiusPx))
            .into(albumCoverImageView)
    }

    private fun setupMediaPlayer(previewUrl: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playButton.isEnabled = true
            }
            setOnCompletionListener {
                playButton.setImageResource(R.drawable.play_button)
                currentTimeTextView .text = "00:00"
            }
        }
    }

    private fun updateProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        val currentPosition = it.currentPosition / 1000
                        currentTimeTextView .text = String.format("%02d:%02d", currentPosition / 60, currentPosition % 60)
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }, 1000)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

}