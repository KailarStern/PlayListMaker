package com.konditsky.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.konditsky.playlistmaker.api.ApiClient
import com.konditsky.playlistmaker.api.TrackResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var noResultsPlaceholder: LinearLayout
    private lateinit var serverErrorPlaceholder: LinearLayout

    //начало активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

            // Изображение ошибка сервера, зависящее от темы
        val imageServerError = findViewById<ImageView>(R.id.imageServerError)
        if (isDarkTheme()) {
            imageServerError.setImageResource(R.drawable.placeholder_dark_no_internet)
        } else {
            imageServerError.setImageResource(R.drawable.placeholder_light_no_internet)
        }

        // Изображение ошибка поиска, зависящее от темы
        val imageNoResults = findViewById<ImageView>(R.id.imageNoResults)
        if (isDarkTheme()) {
            imageNoResults.setImageResource(R.drawable.placeholder_dark_no_results)
        } else {
            imageNoResults.setImageResource(R.drawable.placeholder_light_no_results)
        }


        editTextSearch = findViewById(R.id.editTextSearch) // иниц-ия поле поиска
        val backButton = findViewById<ImageButton>(R.id.buttonSearchBack) // иниц-ия кнопка назад
        recyclerView = findViewById(R.id.recyclerViewSearchResults) // иниц-ия ресайкла
        noResultsPlaceholder = findViewById(R.id.noResultsPlaceholder) // иниц-ия холд поиск
        serverErrorPlaceholder = findViewById(R.id.serverErrorPlaceholder) // иниц-ия холд сервер

        // иниц-ия кнопки повтора и установка слушателя
        val retryButton = findViewById<Button>(R.id.buttonRetry)
        retryButton.setOnClickListener {
            performSearch(editTextSearch.text.toString())
        }

        // слушатель для кнопки назад,для завершения активности
        backButton.setOnClickListener {
            finish()
        }

        updateClearIcon() // Обновляет иконку очистки в поиске

        // Обновляет иконку очистки после изменения текста
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateClearIcon()
            }

            // До изменения текста
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // При изменении текста делает поиск
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
        })

        // Очищает текст и скрывает клавиатуру если нажать крестик
        editTextSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = editTextSearch.compoundDrawables[2]
                if (drawable != null && event.rawX >= (editTextSearch.right - drawable.bounds.width())) {
                    editTextSearch.text.clear()
                    hideKeyboard()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Выполняется поиск если нажать на галочку на клаве
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(editTextSearch.text.toString())
                true
            } else {
                false
            }
        }
    }

    // Поиск и обрабатка поиска по запросу
    private fun performSearch(query: String) {
        serverErrorPlaceholder.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Запрос на поиск по трекам
                val response = ApiClient.instance.search(query)
                if (response.isSuccessful && response.code() == 200) {
                    // Преобразует ответ в список треков
                    val tracks = response.body()?.results?.map { trackResponseToTrack(it) } ?: listOf()
                    withContext(Dispatchers.Main) {
                        if (tracks.isEmpty()) {
                            // Холд если нет результатов
                            noResultsPlaceholder.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            hideKeyboard()
                        } else {
                            // Заполняет и отображает ресайкл результатами поиска
                            noResultsPlaceholder.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            adapter = TrackAdapter(ArrayList(tracks))
                            recyclerView.adapter = adapter
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        // Показывает ошибку если нет инета
                        Toast.makeText(this@SearchActivity, "Ошибка при поиске", Toast.LENGTH_SHORT).show()
                        serverErrorPlaceholder.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        hideKeyboard()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Показывает ошибку если нет результата
                    Toast.makeText(this@SearchActivity, "Произошла ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                    serverErrorPlaceholder.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    hideKeyboard()
                }
            }
        }
    }

    // Конвертирует ответ Айпиай в Трак, показывает время
    private fun trackResponseToTrack(trackResponse: TrackResponse): Track {
        val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackResponse.trackTimeMillis)
        return Track(trackResponse.trackName, trackResponse.artistName, time, trackResponse.artworkUrl100)
    }

    // Отображение иконки очистки если есть текст
    private fun updateClearIcon() {
        if (editTextSearch.text.isNotEmpty()) {
            editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_search, 0, R.drawable.clear, 0)
        } else {
            editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_search, 0, 0, 0)
        }
    }

    // Скрывает клаву
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editTextSearch.windowToken,0)
    }

    // Ключ для запроса
    companion object {
        const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
    }

    // // Сохраняет текст поиска перед закрытием приложения
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, editTextSearch.text.toString())
    }

    // Восстанавливает текст при перезапуске приложения
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedSearchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        editTextSearch.setText(savedSearchQuery)
    }

    // выбирает тему
    private fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}




