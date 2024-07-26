package com.konditsky.playlistmaker.player.data

import android.media.MediaPlayer
import com.konditsky.playlistmaker.player.domain.TrackPlayer

class TrackPlayerRepositoryImpl : TrackPlayer {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var isPlaying: Boolean = false
    private var currentPosition: Int = 0
    private var onCompletion: (() -> Unit)? = null

    override fun prepare(url: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
            currentPosition = 0
            onCompletion?.invoke()
        }
    }

    override fun play() {
        mediaPlayer.start()
        isPlaying = true
        updateCurrentPosition()
    }

    override fun pause() {
        mediaPlayer.pause()
        isPlaying = false
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun setOnCompletionListener(onCompletion: () -> Unit) {
        this.onCompletion = onCompletion
    }

    private fun updateCurrentPosition() {
        currentPosition = mediaPlayer.currentPosition
        if (currentPosition >= 30000) {
            pause()
            mediaPlayer.seekTo(0)
            currentPosition = 0
            onCompletion?.invoke()
        }
    }
}



























