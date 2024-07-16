package com.konditsky.playlistmaker.search.ui

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konditsky.playlistmaker.search.data.api.ItunesResponse
import com.konditsky.playlistmaker.search.domain.SearchHistoryInteractor
import com.konditsky.playlistmaker.search.data.api.ItunesService
import com.konditsky.playlistmaker.search.data.api.TrackResponse
import com.konditsky.playlistmaker.search.ui.Track
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val itunesService: ItunesService
) : ViewModel() {
    private val _trackHistory = MutableLiveData<List<Track>>()
    val trackHistory: LiveData<List<Track>> get() = _trackHistory

    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> get() = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    fun fetchTrackHistory() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getTrackHistory()
            _trackHistory.value = history
        }
    }

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.addTrackToHistory(track)
            fetchTrackHistory()
        }
    }

    fun clearTrackHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearTrackHistory()
            _trackHistory.value = emptyList()
        }
    }

    fun searchTracks(query: String) {
        _isLoading.value = true
        _isError.value = false

        itunesService.search(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val tracks = response.body()?.results?.map { trackResponseToTrack(it) } ?: listOf()
                    _searchResults.value = tracks
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    private fun trackResponseToTrack(trackResponse: TrackResponse): Track {
        return Track(
            trackId = trackResponse.trackId ?: 0L,
            trackName = trackResponse.trackName ?: "Unkno   wn",
            artistName = trackResponse.artistName ?: "Unknown",
            trackTime = trackResponse.trackTimeMillis?.let { SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                Date(it)
            ) } ?: "Unknown",
            artworkUrl100 = trackResponse.artworkUrl100 ?: "",
            collectionName = trackResponse.collectionName,
            releaseDate = trackResponse.releaseDate ?: "",
            primaryGenreName = trackResponse.primaryGenreName ?: "Unknown",
            country = trackResponse.country ?: "Unknown",
            previewUrl = trackResponse.previewUrl ?: ""
        )
    }
}




