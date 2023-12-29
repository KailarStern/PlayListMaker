package com.konditsky.playlistmaker.domain

import android.widget.ImageView

interface ImageLoader {
    fun loadImage(url: String, into: ImageView)
}