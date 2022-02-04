package com.lukakordzaia.core.baseclasses

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.core.R
import com.lukakordzaia.core.utils.createToast
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.core.utils.EventObserver

abstract class BaseFragmentVM<VB: ViewBinding, VM: BaseViewModel> : BaseFragment<VB>() {
    protected abstract val viewModel: VM
    protected abstract val reload: () -> Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
    }

    private fun initListeners() {
        requireActivity().findViewById<Button>(R.id.retry_button)?.setOnClickListener {
            viewModel.setNoInternet(false)
            reload.invoke()
        }
    }

    private fun initObservers() {
        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        viewModel.noInternet.observe(viewLifecycleOwner, {
            requireActivity().findViewById<ConstraintLayout>(R.id.no_internet).setVisibleOrGone(it)
        })
    }
}