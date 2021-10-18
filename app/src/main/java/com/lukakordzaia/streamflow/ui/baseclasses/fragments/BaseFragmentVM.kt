package com.lukakordzaia.streamflow.ui.baseclasses.fragments

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setVisibleOrGone

abstract class BaseFragmentVM<VB: ViewBinding, VM: BaseViewModel> : BaseFragment<VB>() {
    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
    }

    private fun initObservers() {
        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            requireActivity().findViewById<ConstraintLayout>(R.id.no_internet).setVisibleOrGone(it)
        })
    }
}