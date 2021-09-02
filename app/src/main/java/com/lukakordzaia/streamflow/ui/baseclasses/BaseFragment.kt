package com.lukakordzaia.streamflow.ui.baseclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.streamflow.databinding.FragmentTopToolbarBinding
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import org.koin.android.ext.android.inject

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    protected val sharedPreferences: SharedPreferences by inject()

    private var rootView: View? = null

    private var _binding: VB? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    protected val binding: VB
        get() = _binding as VB

    fun topBarListener(header: String, view: FragmentTopToolbarBinding) {
        view.fragmentTopBack.setOnClickListener {
            activity?.onBackPressed()
        }
        view.fragmentTopHeader.text = header
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