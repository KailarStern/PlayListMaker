package com.konditsky.playlistmaker.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): Response<ItunesResponse>

}

data class ItunesResponse(
    val resultCount: Int,
    val results: List<TrackResponse>
)

data class TrackResponse(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)
