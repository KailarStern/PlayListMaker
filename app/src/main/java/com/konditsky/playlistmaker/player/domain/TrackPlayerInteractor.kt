package com.konditsky.playlistmaker.player.domain

import com.konditsky.playlistmaker.player.data.TrackPlayerRepositoryImpl

class TrackPlayerInteractor(private val trackPlayerRepositoryImpl: TrackPlayerRepositoryImpl) {

    fun prepare(url: String) {
        trackPlayerRepositoryImpl.prepare(url)
    }

    fun playOrPause() {
        if (trackPlayerRepositoryImpl.isPlaying()) {
            trackPlayerRepositoryImpl.pause()
        } else {
            trackPlayerRepositoryImpl.play()
        }
    }

    fun isPlaying(): Boolean {
        return trackPlayerRepositoryImpl.isPlaying()
    }

    fun getCurrentPosition(): Int {
        return trackPlayerRepositoryImpl.getCurrentPosition()
    }

    fun release() {
        trackPlayerRepositoryImpl.release()
    }

    fun setOnCompletionListener(onCompletion: () -> Unit) {
        trackPlayerRepositoryImpl.setOnCompletionListener(onCompletion)
    }

    fun play() {
        trackPlayerRepositoryImpl.play()
    }

    fun pause() {
        trackPlayerRepositoryImpl.pause()
    }
}













