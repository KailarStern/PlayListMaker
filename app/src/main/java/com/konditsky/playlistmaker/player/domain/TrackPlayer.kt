package com.konditsky.playlistmaker.player.domain

interface TrackPlayer {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun release()
    fun setOnCompletionListener(onCompletion: () -> Unit)
}
















