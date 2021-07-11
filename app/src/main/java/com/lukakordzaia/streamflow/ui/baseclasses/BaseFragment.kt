package com.lukakordzaia.streamflow.ui.baseclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import kotlinx.android.synthetic.main.fragment_top_toolbar.*
import org.koin.android.ext.android.inject

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    protected val authSharedPreferences: AuthSharedPreferences by inject()
    protected val auth = Firebase.auth

    private var rootView: View? = null

    private var _binding: VB? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    protected val binding: VB
        get() = _binding as VB

    fun topBarListener(header: String) {
        fragment_top_back.setOnClickListener {
            activity?.onBackPressed()
        }
        fragment_top_header.text = header
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return _binding!!.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}