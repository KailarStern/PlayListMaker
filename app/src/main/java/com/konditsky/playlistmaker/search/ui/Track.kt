package com.konditsky.playlistmaker.search.ui

import android.icu.text.SimpleDateFormat
import java.io.Serializable
import java.util.Locale

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable {
    fun getFormattedReleaseYear(): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(releaseDate)
            date?.let { outputFormat.format(it) } ?: "Неизвестно"
        } catch (e: Exception) {
            "Неизвестно"
        }
    }

    fun getHighQualityArtworkUrl(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}



