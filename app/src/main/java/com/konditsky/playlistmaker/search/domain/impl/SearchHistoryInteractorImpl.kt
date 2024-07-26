package com.konditsky.playlistmaker.search.domain.impl

import com.konditsky.playlistmaker.search.data.SearchHistoryManager
import com.konditsky.playlistmaker.search.domain.SearchHistoryInteractor
import com.konditsky.playlistmaker.search.ui.Track

class SearchHistoryInteractorImpl(private val searchHistoryManager: SearchHistoryManager) : SearchHistoryInteractor {
    override fun addTrackToHistory(track: Track) {
        searchHistoryManager.addTrackToHistory(track)
    }

    override fun getTrackHistory(): List<Track> {
        return searchHistoryManager.getTrackHistory()
    }

    override fun clearTrackHistory() {
        searchHistoryManager.clearTrackHistory()
    }
}
