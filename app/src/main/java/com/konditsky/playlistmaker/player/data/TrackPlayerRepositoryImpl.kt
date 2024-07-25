package com.konditsky.playlistmaker.player.data

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.konditsky.playlistmaker.player.domain.TrackPlayer

class TrackPlayerRepositoryImpl : TrackPlayer {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val _isPlayingLiveData = MutableLiveData<Boolean>()
    val isPlayingLiveData: LiveData<Boolean> get() = _isPlayingLiveData

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    override fun prepare(url: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()
        mediaPlayer.setOnCompletionListener {
            _isPlayingLiveData.postValue(false)
            _currentPosition.postValue(0)
        }
    }


    override fun play() {
        mediaPlayer.start()
        _isPlayingLiveData.postValue(true)
        updateCurrentPosition()
    }

    override fun pause() {
        mediaPlayer.pause()
        _isPlayingLiveData.postValue(false)
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
        mediaPlayer.setOnCompletionListener {
            onCompletion()
            _isPlayingLiveData.postValue(false)
            _currentPosition.postValue(0)
        }
    }

    fun updateCurrentPosition() {
        val position = mediaPlayer.currentPosition
        if (position >= 30000) {
            pause()
            mediaPlayer.seekTo(0)
            _currentPosition.postValue(0)
        } else {
            _currentPosition.postValue(position)
        }
}
}
























