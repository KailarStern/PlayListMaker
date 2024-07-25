package com.konditsky.playlistmaker.search.domain

import com.konditsky.playlistmaker.search.data.api.ItunesResponse
import retrofit2.Call

interface TrackRepository {
    fun search(query: String): Call<ItunesResponse>
}
