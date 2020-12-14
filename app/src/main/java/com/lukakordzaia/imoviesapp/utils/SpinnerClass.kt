package com.lukakordzaia.imoviesapp.utils

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.lukakordzaia.imoviesapp.R

class SpinnerClass(private val context: Context) {

    fun createSpinner(view: Spinner, list: List<*>, onItemChosen: (language: String) -> Unit) {
        val adapter = ArrayAdapter(context,
                R.layout.spinner_season_item, list)

        view.adapter = adapter
        view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                onItemChosen(list[position].toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }
}