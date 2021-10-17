package com.lukakordzaia.streamflow.ui.baseclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import org.koin.android.ext.android.inject

abstract class BaseBottomSheetVM<VB: ViewBinding, VM: BaseViewModel> : BottomSheetDialogFragment() {
    protected val sharedPreferences: SharedPreferences by inject()
    protected abstract val viewModel: VM

    private var _binding: VB? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    protected val binding: VB
        get() = _binding as VB

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