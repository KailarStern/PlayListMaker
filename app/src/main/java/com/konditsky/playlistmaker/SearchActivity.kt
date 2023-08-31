package com.konditsky.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton

class SearchActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        val backButton = findViewById<ImageButton>(R.id.buttonSearchBack)


        backButton.setOnClickListener {
            finish()
        }

        updateClearIcon()


        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateClearIcon()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


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
    }

    private fun updateClearIcon() {
        if (editTextSearch.text.isNotEmpty()) {
            editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_search, 0, R.drawable.clear, 0)
        } else {
            editTextSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_search, 0, 0, 0)
        }
    }

    private fun hideKeyboard(){
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editTextSearch.windowToken,0)
    }

    companion object {
        const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, editTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedSearchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        editTextSearch.setText(savedSearchQuery)
    }
}


