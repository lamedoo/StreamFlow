package com.lukakordzaia.streamflow.ui.tv.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.lukakordzaia.streamflow.databinding.FragmentTvLoginBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentVM
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.tv.main.TvActivity
import com.lukakordzaia.streamflow.utils.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvLoginFragment: BaseFragmentVM<FragmentTvLoginBinding, ProfileViewModel>() {
    override val viewModel by viewModel<ProfileViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvLoginBinding
        get() = FragmentTvLoginBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCredentials()
        fragmentListeners()
        fragmentObservers()
        onEditorActionListener(binding.passwordInput)
    }

    private fun setCredentials() {
        if (sharedPreferences.getUsername() != "") {
            binding.usernameInput.setText(sharedPreferences.getUsername())
        }
        if (sharedPreferences.getPassword() != "") {
            binding.passwordInput.setText(sharedPreferences.getPassword())
        }
    }

    private fun fragmentListeners() {
        binding.authButton.setOnClickListener {
            clearFocus(binding.usernameInput)
            clearFocus(binding.passwordInput)

            if (!binding.usernameInput.text.isNullOrEmpty() && !binding.passwordInput.text.isNullOrEmpty()) {
                viewModel.userLogin(
                    PostLoginBody(
                    3,
                    "password",
                    binding.passwordInput.text.toString(),
                    binding.usernameInput.text.toString()
                )
                )
            }
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED -> {
                    val intent = Intent(requireContext(), TvActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        })
    }

    private fun onEditorActionListener(view: EditText) {
        view.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    clearFocus(view)
                    binding.authButton.requestFocus()
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }
    }

    private fun clearFocus(view: EditText) {
        view.apply {
            hideKeyboard()
            clearFocus()
        }
    }
}