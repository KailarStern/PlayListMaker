package com.konditsky.playlistmaker.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesService {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<ItunesResponse>
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

