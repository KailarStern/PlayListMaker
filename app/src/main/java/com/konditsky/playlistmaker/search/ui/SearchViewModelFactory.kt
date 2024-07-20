package com.konditsky.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.konditsky.playlistmaker.search.domain.SearchHistoryInteractor
import com.konditsky.playlistmaker.search.data.api.ItunesService

class SearchViewModelFactory(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val itunesService: ItunesService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchHistoryInteractor, itunesService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
