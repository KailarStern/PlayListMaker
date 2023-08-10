package com.konditsky.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageButton

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val editTextSearch: EditText = findViewById(R.id.editTextSearch)
        val backButton = findViewById<ImageButton>(R.id.buttonSearchBack)


        backButton.setOnClickListener {
            finish()
        }



        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true) {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.clear, 0)
                } else {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        editTextSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = editTextSearch.compoundDrawables[2]
                if (drawable != null && event.rawX >= (editTextSearch.right - drawable.bounds.width())) {
                    editTextSearch.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        outState.putString("SEARCH_QUERY", editTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        val savedSearchQuery = savedInstanceState.getString("SEARCH_QUERY", "")
        editTextSearch.setText(savedSearchQuery)
    }

}


