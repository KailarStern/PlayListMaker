package com.konditsky.playlistmaker.search.domain

import com.konditsky.playlistmaker.search.data.api.ItunesResponse
import retrofit2.Call

interface TrackRepository {
    fun searchTracks(query: String): Call<ItunesResponse>
}
