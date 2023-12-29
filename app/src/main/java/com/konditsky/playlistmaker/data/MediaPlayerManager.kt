package com.konditsky.playlistmaker.data

import android.media.MediaPlayer
import com.konditsky.playlistmaker.domain.TrackPlayer

class MediaPlayerManager : TrackPlayer {
    private val mediaPlayer = MediaPlayer()

    override fun prepare(url: String) {
        mediaPlayer.apply {
            reset()
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}
