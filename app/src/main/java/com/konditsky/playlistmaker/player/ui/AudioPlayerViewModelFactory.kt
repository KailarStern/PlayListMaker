package com.konditsky.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.konditsky.playlistmaker.player.domain.TrackPlayerInteractor

class AudioPlayerViewModelFactory(private val trackPlayerInteractor: TrackPlayerInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            return AudioPlayerViewModel(trackPlayerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}







