    package com.konditsky.playlistmaker.search.data.api

    import retrofit2.Call
    import retrofit2.http.GET
    import retrofit2.http.Query

    interface ItunesService {
        @GET("/search")
        fun search(@Query("term") text: String): Call<ItunesResponse>
    }

    data class ItunesResponse(
        val resultCount: Int,
        val results: List<TrackResponse>
    )

    data class TrackResponse(
        val trackId: Long?,
        val trackName: String?,
        val artistName: String?,
        val trackTimeMillis: Long?,
        val artworkUrl100: String?,
        val collectionName: String?,
        val releaseDate: String?,
        val primaryGenreName: String?,
        val country: String?,
        val previewUrl: String?
    )

