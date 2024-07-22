package com.konditsky.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.konditsky.playlistmaker.player.domain.MediaPlayerManager
import com.konditsky.playlistmaker.player.domain.TrackPlayer

class AudioPlayerViewModel : ViewModel() {
    private val trackPlayer: TrackPlayer = MediaPlayerManager()

    private val _playbackState = MutableLiveData<Boolean>()
    val playbackState: LiveData<Boolean> = _playbackState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    init {
        trackPlayer.setOnCompletionListener {
            _playbackState.value = false
            _currentPosition.value = 0
        }

        (trackPlayer as MediaPlayerManager).currentPosition.observeForever { position ->
            _currentPosition.value = position
        }

        trackPlayer.isPlaying.observeForever { isPlaying ->
            _playbackState.value = isPlaying
        }
    }

    fun prepare(url: String) {
        trackPlayer.prepare(url)
    }

    fun playOrPause() {
        if (trackPlayer.isPlaying()) {
            trackPlayer.pause()
            _playbackState.value = false
        } else {
            trackPlayer.play()
            _playbackState.value = true
            updateCurrentPosition()
        }
    }

    fun updateCurrentPosition() {
        _currentPosition.value = trackPlayer.getCurrentPosition()
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayer.release()
    }
}




















