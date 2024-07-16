package com.konditsky.playlistmaker.search.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konditsky.playlistmaker.R

class TrackAdapter(
    private var trackList: ArrayList<Track>,
    private val onItemClicked: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_music_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClicked(track)
        }
    }

    override fun getItemCount() = trackList.size

    fun updateTracks(newTracks: ArrayList<Track>) {
        Log.d("TrackAdapter", "Updating tracks. New tracks: $newTracks")
        trackList.clear()
        trackList.addAll(newTracks)
        notifyDataSetChanged()
    }
}

