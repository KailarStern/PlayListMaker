package com.konditsky.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.konditsky.playlistmaker.player.domain.TrackPlayerInteractor

class AudioPlayerViewModel(private val trackPlayerInteractor: TrackPlayerInteractor) : ViewModel() {
    private val _isPlayingLiveData = MutableLiveData<Boolean>()
    val isPlayingLiveData: LiveData<Boolean> get() = _isPlayingLiveData

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    init {
        trackPlayerInteractor.setOnCompletionListener {
            _isPlayingLiveData.postValue(false)
            _currentPosition.postValue(0)
        }
    }

    fun prepare(url: String) {
        trackPlayerInteractor.prepare(url)
    }

    fun playOrPause() {
        if (trackPlayerInteractor.isPlaying()) {
            trackPlayerInteractor.pause()
            _isPlayingLiveData.postValue(false)
        } else {
            trackPlayerInteractor.play()
            _isPlayingLiveData.postValue(true)
            updateCurrentPosition()
        }
    }

    fun updateCurrentPosition() {
        _currentPosition.postValue(trackPlayerInteractor.getCurrentPosition())
    }

    fun clearResources() {
        trackPlayerInteractor.release()
    }
}













































