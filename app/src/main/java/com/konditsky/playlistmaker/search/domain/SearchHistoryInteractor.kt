package com.konditsky.playlistmaker.search.domain

import com.konditsky.playlistmaker.search.ui.Track

interface SearchHistoryInteractor {
    fun addTrackToHistory(track: Track)
    fun getTrackHistory(): List<Track>
    fun clearTrackHistory()
}