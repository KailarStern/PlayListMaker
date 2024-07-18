package com.konditsky.playlistmaker.player.data

import android.media.MediaPlayer

class MediaPlayerService {
    private val mediaPlayer = MediaPlayer()

    var onError: ((String) -> Unit)? = null
    var onPrepared: (() -> Unit)? = null
    var onCompletion: (() -> Unit)? = null

    init {
        mediaPlayer.setOnErrorListener { _, what, extra ->
            onError?.invoke("Playback Error: what=$what, extra=$extra")
            true
        }

        mediaPlayer.setOnPreparedListener {
            onPrepared?.invoke()
        }

        mediaPlayer.setOnCompletionListener {
            onCompletion?.invoke()
        }
    }

    fun prepare(url: String) {
        mediaPlayer.apply {
            reset()
            setDataSource(url)
            prepareAsync()
        }
    }

    fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun release() {
        mediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}
