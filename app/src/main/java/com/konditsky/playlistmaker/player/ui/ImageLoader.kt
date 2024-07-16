package com.konditsky.playlistmaker.player.ui

import android.widget.ImageView

interface ImageLoader {
    fun loadImage(url: String, into: ImageView)
}