package com.lukakordzaia.streamflow.ui.baseclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import kotlinx.android.synthetic.main.fragment_top_toolbar.*
import org.koin.android.ext.android.inject

open class BaseFragment() : Fragment() {
    protected val authSharedPreferences: AuthSharedPreferences by inject()
    protected val auth = Firebase.auth

    var hasInitializedRootView = false
    private var rootView: View? = null

    fun topBarListener(header: String) {
        fragment_top_back.setOnClickListener {
            activity?.onBackPressed()
        }

        fragment_top_header.text = header
    }

    fun getPersistentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?, layout: Int): View? {
        if (rootView == null) {
            // Inflate the layout for this fragment
            rootView = inflater?.inflate(layout,container,false)
        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove rootView from the existing parent view group
            // (it will be added back).
            (rootView?.parent as? ViewGroup)?.removeView(rootView)
        }

        return rootView
    }
}