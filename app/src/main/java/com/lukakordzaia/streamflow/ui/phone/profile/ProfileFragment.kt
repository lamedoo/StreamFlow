package com.lukakordzaia.streamflow.ui.phone.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneProfileBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflow.utils.setVisibleOrGone
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragmentPhoneVM<FragmentPhoneProfileBinding, ProfileViewModel>() {
    override val viewModel by viewModel<ProfileViewModel>()
    override val reload: () -> Unit = { viewModel.refreshProfileOnLogin() }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneProfileBinding
        get() = FragmentPhoneProfileBinding::inflate

    override fun onStart() {
        super.onStart()

        viewModel.getUserData()
        updateProfileUI(sharedPreferences.getLoginToken() != "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBarListener(resources.getString(R.string.account), binding.toolbar)

        binding.aboutTitle.text = "ვერსია v${requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName}"

        fragmentListeners()
        fragmentObservers()
    }

    private fun fragmentListeners() {
        binding.gSignIn.setOnClickListener {
            viewModel.onLoginPressed()
        }

        binding.gSignOut.setOnClickListener {
            viewModel.userLogout()
        }

        binding.clearContainer.setOnClickListener {
            val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
            val clearHistory = Dialog(requireContext())
            clearHistory.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearHistory.setContentView(binding.root)

            binding.continueButton.setOnClickListener {
                if (sharedPreferences.getLoginToken() == "") {
                    viewModel.deleteContinueWatchingFromRoomFull()
                }
                clearHistory.dismiss()
            }
            binding.cancelButton.setOnClickListener {
                clearHistory.dismiss()
            }
            clearHistory.show()
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED -> viewModel.refreshProfileOnLogin()
            }
        })
    }

    private fun updateProfileUI(isLoggedIn: Boolean) {
        binding.profileContainer.setVisibleOrGone(isLoggedIn)
        binding.gSignIn.setVisibleOrGone(!isLoggedIn)
        binding.gSignOut.setVisibleOrGone(isLoggedIn)
        binding.lastLine.setVisibleOrGone(isLoggedIn)
        binding.firstLine.setVisibleOrGone(!isLoggedIn)
        binding.clearContainer.setVisibleOrGone(!isLoggedIn)
        binding.historyLine.setVisibleOrGone(!isLoggedIn)

        if (isLoggedIn) {
            viewModel.userData.observe(viewLifecycleOwner, {
                binding.profileUsername.text = it.displayName
                Glide.with(this).load(it.avatar.large).into(binding.profilePhoto)
            })
        }
    }
}