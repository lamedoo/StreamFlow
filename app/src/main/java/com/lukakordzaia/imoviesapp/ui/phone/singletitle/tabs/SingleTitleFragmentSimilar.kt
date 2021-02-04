package com.lukakordzaia.imoviesapp.ui.phone.singletitle.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleTitleFragmentSimilar : Fragment(R.layout.phone_single_title_similar) {
    private val viewModel by viewModel<SingleTitleViewModel>()
    private var titleId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            val id = bundle.getString("titleId")
            titleId = id!!.toInt()
        }
    }
}