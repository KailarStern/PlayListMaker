package com.konditsky.playlistmaker.player.domain

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MediaPlayerManager : TrackPlayer {
    private val mediaPlayer = MediaPlayer()

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    private var onCompletionListener: (() -> Unit)? = null

    init {
        mediaPlayer.setOnErrorListener { _, _, _ -> true }

        mediaPlayer.setOnPreparedListener {
            _isPlaying.value = false
            updateCurrentPosition()
        }

        mediaPlayer.setOnCompletionListener {
            _isPlaying.value = false
            _currentPosition.value = 0
            onCompletionListener?.invoke()
        }
    }

    override fun prepare(url: String) {
        mediaPlayer.apply {
            reset()
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _isPlaying.value = true
            updateCurrentPosition()
        }
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _isPlaying.value = false
        }
    }

    override fun release() {
        mediaPlayer.release()
        _isPlaying.value = false
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnCompletionListener(onCompletion: () -> Unit) {
        onCompletionListener = onCompletion
    }

    private fun updateCurrentPosition() {
        _currentPosition.postValue(mediaPlayer.currentPosition)
    }
}





















