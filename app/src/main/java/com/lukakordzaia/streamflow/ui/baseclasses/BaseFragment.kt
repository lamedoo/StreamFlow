package com.lukakordzaia.streamflow.ui.baseclasses

import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import kotlinx.android.synthetic.main.fragment_top_toolbar.*
import org.koin.android.ext.android.inject

open class BaseFragment(fragment: Int) : Fragment(fragment) {
    protected val authSharedPreferences: AuthSharedPreferences by inject()
    protected val auth = Firebase.auth

    fun topBarListener(header: String) {
        fragment_top_back.setOnClickListener {
            activity?.onBackPressed()
        }

        fragment_top_header.text = header
    }
}