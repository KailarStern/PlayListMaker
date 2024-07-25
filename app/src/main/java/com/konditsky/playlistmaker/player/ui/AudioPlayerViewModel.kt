package com.konditsky.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.konditsky.playlistmaker.player.domain.TrackPlayerInteractor

data class AudioPlayerState(
    val isPlaying: Boolean,
    val currentPosition: Int
)


class AudioPlayerViewModel(private val trackPlayerInteractor: TrackPlayerInteractor) : ViewModel() {

    private val _screenState = MutableLiveData<AudioPlayerState>().apply {
        value = AudioPlayerState(isPlaying = false, currentPosition = 0)
    }

    val screenState: LiveData<AudioPlayerState> = _screenState

    init {
        trackPlayerInteractor.isPlaying.observeForever { isPlaying ->
            _screenState.value = _screenState.value?.copy(isPlaying = isPlaying)
        }

        trackPlayerInteractor.currentPosition.observeForever { position ->
            _screenState.value = _screenState.value?.copy(currentPosition = position)
        }

        trackPlayerInteractor.setOnCompletionListener {
            _screenState.value = AudioPlayerState(isPlaying = false, currentPosition = 0)
        }
    }

    fun prepare(url: String) {
        trackPlayerInteractor.prepare(url)
    }

    fun playOrPause() {
        trackPlayerInteractor.playOrPause()
    }

    fun updateCurrentPosition() {
        val position = trackPlayerInteractor.getCurrentPosition()
        _screenState.value = _screenState.value?.copy(currentPosition = position)
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayerInteractor.release()
    }
}






































