package com.konditsky.playlistmaker

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.konditsky.playlistmaker.api.ApiClient
import com.konditsky.playlistmaker.api.ItunesResponse
import com.konditsky.playlistmaker.api.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale

class SearchActivity : AppCompatActivity() {

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
    companion object {
        const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
        private const val PREFS_NAME = "com.konditsky.playlistmaker.prefs"
        private const val SEARCH_QUERY = "search_query"
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        progressBar = findViewById(R.id.progressBar)
        editTextSearch = findViewById(R.id.editTextSearch)

        trackHistoryManager = SearchHistoryManager(getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
        adapter = TrackAdapter(ArrayList()) { track ->
            itemClickRunnable?.let { itemClickHandler.removeCallbacks(it) }
            itemClickRunnable = Runnable {
                trackHistoryManager.addTrackToHistory(track)
                updateSearchHistoryDisplay()
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("TRACK_DATA", track)
                startActivity(intent)
            }
            itemClickHandler.postDelayed(itemClickRunnable!!, itemClickDelay)
        }

        recyclerView = findViewById(R.id.recyclerViewSearchResults)
        recyclerView.adapter = adapter


        val history = trackHistoryManager.getTrackHistory()
        if (history.isNotEmpty()) {
            adapter.updateTracks(ArrayList(history))
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


        val lastSearchQuery = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(SEARCH_QUERY, "")
        if (!lastSearchQuery.isNullOrEmpty()) {
            editTextSearch.setText(lastSearchQuery)
            performSearch(lastSearchQuery)
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

        setupUIBehavior()
        updateSearchHistoryDisplay()
        setupSearch()
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
            performSearch(editTextSearch.text.toString())
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
                    updateSearchHistoryDisplay()
                } else {
                    performSearch(editTextSearch.text.toString())
                }
                true
            } else {
                false
            }
        }


        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editTextSearch.text.isEmpty()) {
                updateSearchHistoryDisplay()
            } else {
                recyclerView.visibility = View.GONE
            }
        }

        clearHistoryButton.setOnClickListener {
            trackHistoryManager.clearTrackHistory()
            updateSearchHistoryDisplay()
        }
        editTextSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2
                val drawable = editTextSearch.compoundDrawables[drawableRight]
                if (drawable != null && event.rawX >= (editTextSearch.right - drawable.bounds.width())) {
                    editTextSearch.text.clear()
                    clearSearchQueryInPreferences()
                    hideKeyboard()
                    updateSearchHistoryDisplay()
                    return@setOnTouchListener true
                }
            }
            false
        }


    }


    private fun updateSearchHistoryDisplay() {
        val history = trackHistoryManager.getTrackHistory()
        Log.d("SearchHistoryDisplay", "Updating search history display. History size: ${history.size}")

        if (history.isEmpty()) {
            Log.d("SearchHistoryDisplay", "History is empty. Hiding search history UI elements.")
            recyclerView.visibility = View.GONE
            youSearchedView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        } else {
            Log.d("SearchHistoryDisplay", "History is not empty. Showing search history UI elements.")
            adapter.updateTracks(ArrayList(history))
            recyclerView.visibility = View.VISIBLE
            youSearchedView.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
        noResultsPlaceholder.visibility = View.GONE
        serverErrorPlaceholder.visibility = View.GONE

        Log.d("SearchHistoryDisplay", "youSearchedView visibility: ${youSearchedView.visibility}")
        Log.d("SearchHistoryDisplay", "clearHistoryButton visibility: ${clearHistoryButton.visibility}")
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }



    private fun performSearch(query: String) {
        Log.d("SearchActivity", "Performing search with query: $query")
        if (query.isEmpty()) {
            updateSearchHistoryDisplay()
            return
        }
        showProgressBar()
        saveSearchQuery(query)

        youSearchedView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE

        hideKeyboard()

        if (!isInternetAvailable()) {
            serverErrorPlaceholder.visibility = View.VISIBLE
            noResultsPlaceholder.visibility = View.GONE
            recyclerView.visibility = View.GONE
            return
        }

        ApiClient.instance.search(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                Log.d("SearchActivity", "Search response: ${response.body()}")
                hideProgressBar()
                if (response.isSuccessful) {
                    val tracks = response.body()?.results?.map { trackResponseToTrack(it) } ?: listOf()
                    if (tracks.isEmpty()) {
                        noResultsPlaceholder.visibility = View.VISIBLE
                        serverErrorPlaceholder.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    } else {
                        adapter.updateTracks(ArrayList(tracks))
                        recyclerView.visibility = View.VISIBLE
                        noResultsPlaceholder.visibility = View.GONE
                        serverErrorPlaceholder.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@SearchActivity, "Ошибка при поиске", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                Log.e("SearchActivity", "Search error: ${t.message}")
                hideProgressBar()
                serverErrorPlaceholder.visibility = View.VISIBLE
                noResultsPlaceholder.visibility = View.GONE
                recyclerView.visibility = View.GONE
                Toast.makeText(this@SearchActivity, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun trackResponseToTrack(trackResponse: TrackResponse): Track {
        val timeMillis = trackResponse.trackTimeMillis?.let { Date(it) }
        val time = timeMillis?.let { SimpleDateFormat("mm:ss", Locale.getDefault()).format(it) } ?: "Unknown Time"

        return Track(
            trackId = 0L,
            trackName = trackResponse.trackName ?: "Unknown Track",
            artistName = trackResponse.artistName ?: "Unknown Artist",
            trackTime = time,
            artworkUrl100 = trackResponse.artworkUrl100 ?: "Default URL",
            collectionName = trackResponse.collectionName,
            releaseDate = trackResponse.releaseDate ?: "Unknown Release Date",
            primaryGenreName = trackResponse.primaryGenreName ?: "Unknown Genre",
            country = trackResponse.country ?: "Unknown Country",
            previewUrl = trackResponse.previewUrl ?: "Default Preview URL"
        )
    }



    private fun updateClearIcon() {
        if (editTextSearch.text.isNotEmpty()) {
            editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_search, 0, R.drawable.clear, 0)
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
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedSearchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY) ?: ""
        if (savedSearchQuery.isNotEmpty()) {
            editTextSearch.setText(savedSearchQuery)
            performSearch(savedSearchQuery)
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
        recyclerView.visibility = View.VISIBLE
        if (trackHistoryManager.getTrackHistory().isEmpty()) {
            noResultsPlaceholder.visibility = View.VISIBLE
        } else {
            youSearchedView.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }



}



