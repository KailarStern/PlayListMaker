package com.konditsky.playlistmaker.search.data.impl

import com.konditsky.playlistmaker.search.data.api.ApiClient
import com.konditsky.playlistmaker.search.data.api.ItunesResponse
import com.konditsky.playlistmaker.search.domain.TrackRepository
import retrofit2.Call

class TrackRepositoryImpl : TrackRepository {
    override fun searchTracks(query: String): Call<ItunesResponse> {
        return ApiClient.instance.search(query)
    }
}


