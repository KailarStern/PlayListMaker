package com.konditsky.playlistmaker

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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.konditsky.playlistmaker.api.ApiClient
import com.konditsky.playlistmaker.api.ItunesResponse
import com.konditsky.playlistmaker.api.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        noResultsPlaceholder.visibility = View.GONE
        serverErrorPlaceholder.visibility = View.GONE
        //  Поиск через ретрофит
        ApiClient.instance.search(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                if (response.isSuccessful) {
                    // Если ответ успешный, обрабатываем данные.
                    val tracks =
                        response.body()?.results?.map { trackResponseToTrack(it) } ?: listOf()
                    if (tracks.isEmpty()) {
                        // Если нет результатов, то полейсхолд.
                        noResultsPlaceholder.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        // Или даём результаты ресайкла
                        noResultsPlaceholder.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter = TrackAdapter(ArrayList(tracks))
                        recyclerView.adapter = adapter
                    }
                    hideKeyboard()
                } else {
                    // Если ответ неуспешный плейсхолд
                    serverErrorPlaceholder.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    hideKeyboard()
                    Toast.makeText(
                        this@SearchActivity,
                        "Ошибка при поиске",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }



            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                // Если запрос не выполнен, показываем плейсхо
                noResultsPlaceholder.visibility = View.GONE // скрыть плейсхолдер "нет результатов" при ошибке
                serverErrorPlaceholder.visibility = View.VISIBLE // показать плейсхолдер "ошибка сервера"
                recyclerView.visibility = View.GONE // скрыть ресайкл
                    // hideKeyboard() //это строка типа лишняя
                Toast.makeText(
                    this@SearchActivity,
                    "Произошла ошибка: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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




