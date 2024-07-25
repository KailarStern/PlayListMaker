package com.konditsky.playlistmaker.search.data

import com.konditsky.playlistmaker.search.data.api.ApiClient
import com.konditsky.playlistmaker.search.data.api.ItunesService
import com.konditsky.playlistmaker.search.data.api.ItunesResponse
import com.konditsky.playlistmaker.search.domain.TrackRepository
import retrofit2.Call

class TrackRepositoryImpl(
    private val itunesService: ItunesService = ApiClient.instance
) : TrackRepository {
    override fun search(query: String): Call<ItunesResponse> {
        return itunesService.search(query)
    }
}
