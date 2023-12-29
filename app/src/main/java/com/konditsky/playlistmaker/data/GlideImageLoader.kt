package com.konditsky.playlistmaker.data

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.konditsky.playlistmaker.domain.ImageLoader

class GlideImageLoader : ImageLoader {
    override fun loadImage(url: String, into: ImageView) {
        Glide.with(into.context).load(url).into(into)
    }
}