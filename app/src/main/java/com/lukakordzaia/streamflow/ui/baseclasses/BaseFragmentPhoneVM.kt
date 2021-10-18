package com.lukakordzaia.streamflow.ui.baseclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentTopToolbarBinding
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.navController
import com.lukakordzaia.streamflow.utils.setVisibleOrGone
import org.koin.android.ext.android.inject

abstract class BaseFragmentPhoneVM<VB: ViewBinding, VM: BaseViewModel> : Fragment() {
    protected val sharedPreferences: SharedPreferences by inject()
    protected abstract val viewModel: VM
    protected abstract val reload: () -> Unit

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return _binding!!.root
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

        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            requireActivity().findViewById<ConstraintLayout>(R.id.no_internet).setVisibleOrGone(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}