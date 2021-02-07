package com.lukakordzaia.imoviesapp.helpers

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.lukakordzaia.imoviesapp.R

class SpinnerClass(private val context: Context) {

    fun createSpinner(view: Spinner, list: List<*>, onItemChosen: (language: String) -> Unit) {
        val adapter = ArrayAdapter(context, R.layout.spinner_item, list)

        view.adapter = adapter
        view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                onItemChosen(list[position].toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }
}