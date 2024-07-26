package com.konditsky.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.konditsky.playlistmaker.search.domain.SearchHistoryInteractor
import com.konditsky.playlistmaker.search.domain.TrackRepository
import com.konditsky.playlistmaker.search.data.impl.TrackRepositoryImpl

class SearchViewModelFactory(
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModelProvider.Factory {
    private val trackRepository: TrackRepository = TrackRepositoryImpl()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchHistoryInteractor, trackRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}









