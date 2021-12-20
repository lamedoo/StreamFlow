package com.lukakordzaia.streamflowphone.ui.baseclasses

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.core.baseclasses.BaseFragmentVM
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.utils.navController
import com.lukakordzaia.streamflowphone.databinding.FragmentTopToolbarBinding
import com.lukakordzaia.core.utils.EventObserver

abstract class BaseFragmentPhoneVM<VB: ViewBinding, VM: BaseViewModel> : BaseFragmentVM<VB, VM>() {

    fun topBarListener(header: String, view: FragmentTopToolbarBinding) {
        view.fragmentTopBack.setOnClickListener {
            activity?.onBackPressed()
        }
        view.fragmentTopHeader.text = header
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}