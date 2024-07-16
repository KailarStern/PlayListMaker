package com.konditsky.playlistmaker.player.ui

import android.widget.ImageView
import com.bumptech.glide.Glide

class GlideImageLoader : ImageLoader {
    override fun loadImage(url: String, into: ImageView) {
        Glide.with(into.context).load(url).into(into)
    }
}