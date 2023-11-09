package com.konditsky.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private var trackList: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    private var onItemClickListener:((Track) -> Unit)? = null

    fun setOnItemClickListener(listener:(Track)-> Unit){
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_music_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(track)
        }


    }

    override fun getItemCount() = trackList.size

    fun updateTracks(newTracks: ArrayList<Track>) {
        trackList.clear()
        trackList.addAll(newTracks)
        notifyDataSetChanged()
    }
}
