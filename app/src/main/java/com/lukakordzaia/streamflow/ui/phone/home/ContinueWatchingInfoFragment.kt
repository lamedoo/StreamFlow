package com.lukakordzaia.streamflow.ui.phone.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneContinueWatchingInfoBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseBottomSheet
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.navController
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContinueWatchingInfoFragment : BaseBottomSheet<FragmentPhoneContinueWatchingInfoBinding>() {
    private val homeViewModel: HomeViewModel by viewModel()
    private val args: ContinueWatchingInfoFragmentArgs by navArgs()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneContinueWatchingInfoBinding
        get() = FragmentPhoneContinueWatchingInfoBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleName.text = args.titleName

        fragmentListeners()
        fragmentObservers()
    }

    private fun fragmentListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.titleDetails.setOnClickListener {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_CONTINUE_WATCHING_TO_SINGLE, args.titleId)
        }

        binding.removeTitle.setOnClickListener {
            val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
            val removeTitle = Dialog(requireContext())
            removeTitle.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            removeTitle.setContentView(binding.root)

            if (authSharedPreferences.getLoginToken() != "") {
                binding.title.text = resources.getString(R.string.remove_from_list_title)
            }

           binding.continueButton.setOnClickListener {
               if (authSharedPreferences.getLoginToken() == "") {
                   homeViewModel.deleteContinueWatching(args.titleId)
               } else {
                   homeViewModel.hideContinueWatchingLoader.observe(viewLifecycleOwner, {
                       when (it.status) {
                           LoadingState.Status.RUNNING -> {}
                           LoadingState.Status.SUCCESS -> {
                               removeTitle.dismiss()
                               dismiss()
                           }
                       }
                   })
               }
            }
            binding.cancelButton.setOnClickListener {
                removeTitle.dismiss()
            }
            removeTitle.show()
        }
    }

    private fun fragmentObservers() {
        homeViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        homeViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }
}