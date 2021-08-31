package com.lukakordzaia.streamflow.ui.phone.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentPhoneLoginBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment: BaseFragment<FragmentPhoneLoginBinding>() {
    private val profileViewModel: ProfileViewModel by viewModel()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneLoginBinding
        get() = FragmentPhoneLoginBinding::inflate



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBarListener(resources.getString(R.string.authorization), binding.toolbar)

        setCredentials()
        fragmentListeners()
        fragmentObservers()
    }

    private fun setCredentials() {
        if (authSharedPreferences.getUsername() != "") {
            binding.usernameInput.setText(authSharedPreferences.getUsername())
        }
        if (authSharedPreferences.getPassword() != "") {
            binding.passwordInput.setText(authSharedPreferences.getPassword())
        }
    }

    private fun fragmentListeners() {
        binding.authButton.setOnClickListener {
            clearFocus(binding.usernameInput)
            clearFocus(binding.passwordInput)

            if (!binding.usernameInput.text.isNullOrEmpty() && !binding.passwordInput.text.isNullOrEmpty()) {
                profileViewModel.userLogin(PostLoginBody(
                    3,
                    "password",
                    binding.passwordInput.text.toString(),
                    binding.usernameInput.text.toString()
                ))
            }
        }

        onEditorActionListener(binding.usernameInput)
        onEditorActionListener(binding.passwordInput)
    }

    private fun fragmentObservers() {
        profileViewModel.loginLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {}
                LoadingState.Status.SUCCESS -> requireActivity().onBackPressed()
            }
        })
    }

    private fun clearFocus(view: TextInputEditText) {
        view.apply {
            hideKeyboard()
            clearFocus()
        }
    }

    private fun onEditorActionListener(view: TextInputEditText) {
        view.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    clearFocus(view)
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }
    }
}