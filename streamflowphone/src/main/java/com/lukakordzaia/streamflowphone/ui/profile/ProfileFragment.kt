package com.lukakordzaia.streamflowphone.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.DialogUtils
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneProfileBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseFragmentPhoneVM
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragmentPhoneVM<FragmentPhoneProfileBinding, ProfileViewModel>() {
    override val viewModel by viewModel<ProfileViewModel>()
    override val reload: () -> Unit = { viewModel.refreshProfileOnLogin() }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneProfileBinding
        get() = FragmentPhoneProfileBinding::inflate

    override fun onStart() {
        super.onStart()

        if (sharedPreferences.getLoginToken() != "") {
            viewModel.getUserData()
        }
        updateProfileUI(sharedPreferences.getLoginToken() != "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBarListener(resources.getString(R.string.account), binding.toolbar)

        binding.aboutTitle.text = getString(R.string.version_number, requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName)

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
            DialogUtils.generalAlertDialog(
                requireContext(),
                R.string.dialog_remove_history_title,
                R.drawable.icon_remove
            ) {
                if (sharedPreferences.getLoginToken() == "") {
                    viewModel.deleteContinueWatchingFromRoomFull()
                }
            }
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            if (it == LoadingState.LOADED) {
                viewModel.refreshProfileOnLogin()
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