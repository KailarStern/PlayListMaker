package com.konditsky.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()

    fun addTrackToHistory(track: Track) {
        val tracks = getTrackHistory().toMutableList()
        tracks.removeIf { it.trackName == track.trackName && it.artistName == track.artistName }
        tracks.add(0, track)
        while (tracks.size > 10) {
            tracks.removeAt(tracks.size - 1)
        }
        saveTrackHistory(tracks)
    }

    fun getTrackHistory(): List<Track> {
        val json = sharedPreferences.getString("track_history", null)
        if (json == null) {
            return emptyList()
        }
        val type = object : TypeToken<List<Track>>() {}.type
        val result = gson.fromJson<List<Track>>(json, type)

        Log.d("SearchHistoryManager", "Deserialized track history: $result")

        return result ?: emptyList()
    }


    fun clearTrackHistory() {
        sharedPreferences.edit().remove("track_history").apply()
    }

    private fun saveTrackHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString("track_history", json).apply()
    }
}
