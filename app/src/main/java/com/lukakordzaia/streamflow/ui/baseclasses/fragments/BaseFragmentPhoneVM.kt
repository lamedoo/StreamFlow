package com.lukakordzaia.streamflow.ui.baseclasses.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentTopToolbarBinding
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.navController

abstract class BaseFragmentPhoneVM<VB: ViewBinding, VM: BaseViewModel> : BaseFragmentVM<VB, VM>() {
    protected abstract val reload: () -> Unit

    fun topBarListener(header: String, view: FragmentTopToolbarBinding) {
        view.fragmentTopBack.setOnClickListener {
            activity?.onBackPressed()
        }
        view.fragmentTopHeader.text = header
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    private fun initListeners() {
        requireActivity().findViewById<Button>(R.id.retry_button).setOnClickListener {
            reload.invoke()
        }
    }

    private fun initObservers() {
        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}