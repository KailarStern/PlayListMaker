package com.konditsky.playlistmaker.domain

interface TrackPlayer {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
}