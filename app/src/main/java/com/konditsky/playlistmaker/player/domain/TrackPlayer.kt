package com.konditsky.playlistmaker.player.domain

interface TrackPlayer {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
}

