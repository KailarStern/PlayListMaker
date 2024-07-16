    package com.konditsky.playlistmaker.search.ui

    import android.annotation.SuppressLint
    import android.content.Context
    import android.content.Intent
    import android.content.res.Configuration
    import android.icu.text.SimpleDateFormat
    import android.net.ConnectivityManager
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import android.text.Editable
    import android.text.TextWatcher
    import android.util.Log
    import android.view.MotionEvent
    import android.view.View
    import android.view.inputmethod.EditorInfo
    import android.view.inputmethod.InputMethodManager
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.LinearLayout
    import android.widget.ProgressBar
    import android.widget.TextView
    import androidx.activity.viewModels
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import androidx.recyclerview.widget.RecyclerView
    import com.konditsky.playlistmaker.R
    import com.konditsky.playlistmaker.player.ui.AudioPlayerActivity
    import com.konditsky.playlistmaker.search.data.SearchHistoryManager
    import com.konditsky.playlistmaker.search.data.api.ApiClient
    import com.konditsky.playlistmaker.search.data.api.ItunesResponse
    import com.konditsky.playlistmaker.search.data.api.TrackResponse
    import com.konditsky.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import java.util.Date
    import java.util.Locale




    class SearchActivity : AppCompatActivity() {
        private val viewModel: SearchViewModel by viewModels {
            SearchViewModelFactory(
                SearchHistoryInteractorImpl(SearchHistoryManager(getSharedPreferences(PREFS_NAME, MODE_PRIVATE))),
                ApiClient.instance
            )
        }


        private lateinit var editTextSearch: EditText
        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: TrackAdapter
        private lateinit var noResultsPlaceholder: LinearLayout
        private lateinit var serverErrorPlaceholder: LinearLayout
        private lateinit var trackHistoryManager: SearchHistoryManager
        private lateinit var youSearchedView: TextView
        private lateinit var clearHistoryButton: Button
        private lateinit var progressBar: ProgressBar
        private lateinit var searchRunnable: Runnable


        private val searchHandler = Handler(Looper.getMainLooper())
        private val searchDelay = 2000L
        private val itemClickHandler = Handler(Looper.getMainLooper())
        private var itemClickRunnable: Runnable? = null
        private val itemClickDelay = 500L



        @SuppressLint("ClickableViewAccessibility")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_search)

            progressBar = findViewById(R.id.progressBar)
            editTextSearch = findViewById(R.id.editTextSearch)

            adapter = TrackAdapter(ArrayList()) { track ->
                itemClickRunnable?.let { itemClickHandler.removeCallbacks(it) }
                itemClickRunnable = Runnable {
                    viewModel.addTrackToHistory(track)
                    val intent = Intent(this, AudioPlayerActivity::class.java)
                    intent.putExtra("TRACK_DATA", track)
                    startActivity(intent)
                }
                itemClickHandler.postDelayed(itemClickRunnable!!, itemClickDelay)
            }

            recyclerView = findViewById(R.id.recyclerViewSearchResults)
            recyclerView.adapter = adapter

            viewModel.trackHistory.observe(this, Observer { history ->
                if (history.isEmpty()) {
                    noResultsPlaceholder.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    youSearchedView.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                } else {
                    adapter.updateTracks(ArrayList(history))
                    recyclerView.visibility = View.VISIBLE
                    noResultsPlaceholder.visibility = View.GONE
                    youSearchedView.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                }
            })

            viewModel.searchResults.observe(this, Observer { results ->
                adapter.updateTracks(ArrayList(results))
                if (results.isEmpty()) {
                    noResultsPlaceholder.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    noResultsPlaceholder.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            })

            viewModel.isLoading.observe(this, Observer { isLoading ->
                if (isLoading) {
                    showProgressBar()
                } else {
                    hideProgressBar()
                }
            })

            viewModel.isError.observe(this, Observer { isError ->
                if (isError) {
                    serverErrorPlaceholder.visibility = View.VISIBLE
                    noResultsPlaceholder.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                } else {
                    serverErrorPlaceholder.visibility = View.GONE
                }
            })

            if (savedInstanceState != null) {
                val isSearching = savedInstanceState.getBoolean("isSearching", false)
                if (isSearching) {
                    showProgressBar()
                }
            }

            editTextSearch = findViewById(R.id.editTextSearch)
            val backButton = findViewById<ImageButton>(R.id.buttonSearchBack)
            noResultsPlaceholder = findViewById(R.id.noResultsPlaceholder)
            serverErrorPlaceholder = findViewById(R.id.serverErrorPlaceholder)
            youSearchedView = findViewById(R.id.textViewSearchHistoryLabel)
            clearHistoryButton = findViewById(R.id.buttonClearSearchHistory)
            progressBar = findViewById(R.id.progressBar)


            backButton.setOnClickListener {
                finish()
            }



            val imageServerError = findViewById<ImageView>(R.id.imageServerError)
            val imageNoResults = findViewById<ImageView>(R.id.imageNoResults)
            if (isDarkTheme()) {
                imageServerError.setImageResource(R.drawable.placeholder_dark_no_internet)
                imageNoResults.setImageResource(R.drawable.placeholder_dark_no_results)
            } else {
                imageServerError.setImageResource(R.drawable.placeholder_light_no_internet)
                imageNoResults.setImageResource(R.drawable.placeholder_light_no_results)
            }

            progressBar.visibility = View.GONE
            setupUIBehavior()
            setupSearch()
            viewModel.fetchTrackHistory()
        }

        private fun setupSearch() {
            searchRunnable = Runnable {
                performSearch(editTextSearch.text.toString())
            }

            editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    searchHandler.removeCallbacks(searchRunnable)
                    searchHandler.postDelayed(searchRunnable, searchDelay)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }




        private fun setupUIBehavior() {
            val retryButton = findViewById<Button>(R.id.buttonRetry)
            retryButton.setOnClickListener {
                viewModel.searchTracks(editTextSearch.text.toString())
            }

            val backButton = findViewById<ImageButton>(R.id.buttonSearchBack)
            backButton.setOnClickListener {
                finish()
            }

            editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    updateClearIcon()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })


            editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (editTextSearch.text.toString().isEmpty()) {
                        hideKeyboard()
                        viewModel.fetchTrackHistory()
                    } else {
                        viewModel.searchTracks(editTextSearch.text.toString())
                    }
                    true
                } else {
                    false
                }
            }


            editTextSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && editTextSearch.text.isEmpty()) {
                    viewModel.fetchTrackHistory()
                } else {
                    recyclerView.visibility = View.GONE
                }
            }

            clearHistoryButton = findViewById(R.id.buttonClearSearchHistory)
            clearHistoryButton.setOnClickListener {
                viewModel.clearTrackHistory()

            }
            editTextSearch.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableRight = 2
                    val drawable = editTextSearch.compoundDrawables[drawableRight]
                    if (drawable != null && event.rawX >= (editTextSearch.right - drawable.bounds.width())) {
                        editTextSearch.text.clear()
                        clearSearchQueryInPreferences()
                        hideKeyboard()
                        viewModel.fetchTrackHistory()
                        return@setOnTouchListener true
                    }
                }
                false
            }


        }


        private fun updateSearchHistoryDisplay() {
            val history = trackHistoryManager.getTrackHistory()
            if (history.isEmpty() || serverErrorPlaceholder.visibility == View.VISIBLE || noResultsPlaceholder.visibility == View.VISIBLE) {
                recyclerView.visibility = View.GONE
                youSearchedView.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            } else {
                adapter.updateTracks(ArrayList(history))
                recyclerView.visibility = View.VISIBLE
                youSearchedView.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
            }
            noResultsPlaceholder.visibility = View.GONE
            serverErrorPlaceholder.visibility = View.GONE

        }


        private fun isInternetAvailable(): Boolean {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }



        private fun performSearch(query: String) {
            if (query.isEmpty()) {
                hideProgressBar()
                updateSearchHistoryDisplay()
                return
            }
            Log.d("Search", "Searching for: $query")
            showProgressBar()
            saveSearchQuery(query)

            youSearchedView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE

            hideKeyboard()

            if (!isInternetAvailable()) {
                serverErrorPlaceholder.visibility = View.VISIBLE
                noResultsPlaceholder.visibility = View.GONE
                recyclerView.visibility = View.GONE
                youSearchedView.visibility = View.INVISIBLE
                clearHistoryButton.visibility = View.INVISIBLE
                Log.d("Search", "No internet available")
                return
            }

            ApiClient.instance.search(query).enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                    hideProgressBar()
                    if (response.isSuccessful) {
                        val tracks = response.body()?.results?.map { trackResponseToTrack(it) } ?: listOf()
                        Log.d("SearchSuccess", "Number of tracks found: ${tracks.size}")
                        if (tracks.isEmpty()) {
                            noResultsPlaceholder.visibility = View.VISIBLE
                            serverErrorPlaceholder.visibility = View.GONE
                            recyclerView.visibility = View.GONE
                            youSearchedView.visibility = View.GONE
                            clearHistoryButton.visibility = View.GONE
                        } else {
                            adapter.updateTracks(ArrayList(tracks))
                            recyclerView.visibility = View.VISIBLE
                            noResultsPlaceholder.visibility = View.GONE
                            serverErrorPlaceholder.visibility = View.GONE
                            youSearchedView.visibility = View.GONE
                            clearHistoryButton.visibility = View.GONE
                        }
                    } else {
                        Log.d("SearchError", "Error Response Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    hideProgressBar()
                    serverErrorPlaceholder.visibility = View.VISIBLE
                    noResultsPlaceholder.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    youSearchedView.visibility = View.INVISIBLE
                    clearHistoryButton.visibility = View.INVISIBLE
                }
            })
        }


        private fun trackResponseToTrack(trackResponse: TrackResponse): Track {
            val timeMillis = trackResponse.trackTimeMillis?.let { Date(it) }
            val time = timeMillis?.let { SimpleDateFormat("mm:ss", Locale.getDefault()).format(it) } ?: "Unknown Time"

            return Track(
                trackId = 0L,
                trackName = trackResponse.trackName ?: getString(R.string.unknown_track),
                artistName = trackResponse.artistName ?: getString(R.string.unknown_artist),
                trackTime = time,
                artworkUrl100 = trackResponse.artworkUrl100 ?: getString(R.string.default_url),
                collectionName = trackResponse.collectionName,
                releaseDate = trackResponse.releaseDate ?: getString(R.string.unknown_release_date),
                primaryGenreName = trackResponse.primaryGenreName ?: getString(R.string.unknown_genre),
                country = trackResponse.country ?: getString(R.string.unknown_country),
                previewUrl = trackResponse.previewUrl ?: getString(R.string.default_preview_url)
            )
        }



        private fun updateClearIcon() {
            if (editTextSearch.text.isNotEmpty()) {
                editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.button_search, 0,
                    R.drawable.clear, 0)
            } else {
                editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_search, 0, 0, 0)
            }
        }

        private fun hideKeyboard() {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editTextSearch.windowToken,0)
        }

        private fun saveSearchQuery(query: String) {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            if (query.isEmpty()) {
                prefs.remove(SEARCH_QUERY)
            } else {
                prefs.putString(SEARCH_QUERY, query)
            }
            prefs.apply()
        }

        private fun clearSearchQueryInPreferences() {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.remove(SEARCH_QUERY)
            prefs.apply()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString(SEARCH_QUERY_KEY, editTextSearch.text.toString())
            outState.putBoolean("isSearching", progressBar.visibility == View.VISIBLE)
        }


        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            super.onRestoreInstanceState(savedInstanceState)
            val savedSearchQuery = savedInstanceState.getString(SEARCH_QUERY) ?: ""
            if (savedSearchQuery.isNotEmpty()) {
                editTextSearch.setText(savedSearchQuery)
                viewModel.searchTracks(savedSearchQuery)
            }
        }

        private fun isDarkTheme(): Boolean {
            return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        private fun showProgressBar() {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            noResultsPlaceholder.visibility = View.GONE
            serverErrorPlaceholder.visibility = View.GONE
            youSearchedView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }

        private fun hideProgressBar() {
            progressBar.visibility = View.GONE
            if (viewModel.trackHistory.value.isNullOrEmpty()) {
                noResultsPlaceholder.visibility = View.VISIBLE
                youSearchedView.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
                youSearchedView.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
            }
        }


        companion object {
            const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
            private const val PREFS_NAME = "com.konditsky.playlistmaker.prefs"
            private const val SEARCH_QUERY = "search_query"
        }

    }



