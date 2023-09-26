package com.konditsky.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val trackNameTextView: TextView = view.findViewById(R.id.textTrackName)
    private val artistNameTextView: TextView = view.findViewById(R.id.textArtistName)
    private val trackTimeTextView: TextView = view.findViewById(R.id.textTrackTime)
    private val albumImageView: ImageView = view.findViewById(R.id.imageAlbum)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.trackTime
        Glide.with(albumImageView.context)
            .load(track.artworkUrl100)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(8)))
            .placeholder(R.drawable.ic_mock)
            .into(albumImageView)
    }
}
