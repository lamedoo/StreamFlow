package com.lukakordzaia.streamflowphone.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneContinueWatchingInfoBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseBottomSheetVM
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ContinueWatchingInfoFragment : BaseBottomSheetVM<FragmentPhoneContinueWatchingInfoBinding, HomeViewModel>() {
    override val viewModel by sharedViewModel<HomeViewModel>()
    override val reload: () -> Unit = {}

    private val args: ContinueWatchingInfoFragmentArgs by navArgs()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneContinueWatchingInfoBinding
        get() = FragmentPhoneContinueWatchingInfoBinding::inflate

    private lateinit var removeTitleDialog: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentSetUi()
        fragmentObservers()
        fragmentListeners()
    }

    private fun fragmentSetUi() {
        binding.titleName.text = args.titleName
    }

    private fun fragmentListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.titleDetails.setOnClickListener {
            viewModel.onSingleTitlePressed(AppConstants.NAV_CONTINUE_WATCHING_TO_SINGLE, args.titleId)
        }

        binding.removeTitle.setOnClickListener {
            removeTitleDialog()
        }
    }

    private fun fragmentObservers() {
        viewModel.hideContinueWatchingLoader.observe(viewLifecycleOwner, {
            if (it == LoadingState.LOADED) {
                removeTitleDialog.dismiss()
                dismiss()
            }
        })
    }

    private fun removeTitleDialog() {
        val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
        removeTitleDialog = Dialog(requireContext())
        removeTitleDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        removeTitleDialog.setContentView(binding.root)

        if (sharedPreferences.getLoginToken() != "") {
            binding.title.text = resources.getString(R.string.remove_from_list_title)
        }

        binding.continueButton.setOnClickListener {
            viewModel.deleteContinueWatching(args.titleId)
        }

        binding.cancelButton.setOnClickListener {
            removeTitleDialog.dismiss()
        }

        removeTitleDialog.show()
    }
}