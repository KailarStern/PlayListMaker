package com.konditsky.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.konditsky.playlistmaker.search.domain.TrackRepository
import com.konditsky.playlistmaker.search.domain.SearchHistoryInteractor

class SearchViewModelFactory(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val trackRepository: TrackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchHistoryInteractor, trackRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



