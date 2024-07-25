package com.konditsky.playlistmaker.player.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.konditsky.playlistmaker.player.data.TrackPlayerRepositoryImpl

class TrackPlayerInteractor(private val trackPlayer: TrackPlayer) {

    val isPlaying: LiveData<Boolean> = (trackPlayer as? TrackPlayerRepositoryImpl)?.isPlayingLiveData ?: MutableLiveData(false)
    val currentPosition: LiveData<Int> = (trackPlayer as? TrackPlayerRepositoryImpl)?.currentPosition ?: MutableLiveData(0)

    fun prepare(url: String) {
        trackPlayer.prepare(url)
    }

    fun playOrPause() {
        if (trackPlayer.isPlaying()) {
            trackPlayer.pause()
        } else {
            trackPlayer.play()
            updateCurrentPosition()
        }
    }

    fun getCurrentPosition(): Int {
        return trackPlayer.getCurrentPosition()
    }

    private fun updateCurrentPosition() {
        (trackPlayer as? TrackPlayerRepositoryImpl)?.updateCurrentPosition()
    }

    fun release() {
        trackPlayer.release()
    }

    fun setOnCompletionListener(onCompletion: () -> Unit) {
        trackPlayer.setOnCompletionListener(onCompletion)
    }

}









