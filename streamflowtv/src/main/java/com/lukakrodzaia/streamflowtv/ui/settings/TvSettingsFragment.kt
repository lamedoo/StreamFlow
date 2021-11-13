package com.lukakrodzaia.streamflowtv.ui.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukakordzaia.core.baseclasses.BaseFragmentVM
import com.lukakordzaia.core.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.core.utils.setGone
import com.lukakrodzaia.streamflowtv.databinding.FragmentTvSettingsBinding
import com.lukakrodzaia.streamflowtv.interfaces.OnSettingsSelected
import com.lukakrodzaia.streamflowtv.ui.login.TvProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSettingsFragment : BaseFragmentVM<FragmentTvSettingsBinding, TvProfileViewModel>() {
    override val viewModel by viewModel<TvProfileViewModel>()
    override val reload: () -> Unit = { viewModel.getUserData() }

    private var onSettingsSelected: OnSettingsSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSettingsSelected = context as? OnSettingsSelected
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvSettingsBinding
        get() = FragmentTvSettingsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authCheck()
        fragmentFocusListeners()
        fragmentClickListeners()
    }

    private fun authCheck() {
        if (sharedPreferences.getLoginToken() == "") {
            binding.tvSettingsSignout.setGone()
        } else {
            binding.tvSettingsDelete.setGone()
        }
    }

    private fun fragmentFocusListeners() {
        binding.tvSettingsInfo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(1)
            }
        }
        binding.tvSettingsDelete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(2)
            }
        }
        binding.tvSettingsSignout.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(3)
            }
        }
    }

    private fun fragmentClickListeners() {
        binding.tvSettingsInfo.setOnClickListener {
            viewModel.newToastMessage("ინფორმაცია აპლიკაციის შესახებ")
        }

        binding.tvSettingsDelete.setOnClickListener {
            val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.setContentView(binding.root)

            binding.continueButton.setOnClickListener {
                viewModel.deleteContinueWatchingFromRoomFull()

                val intent = Intent(requireContext(), TvSettingsActivity::class.java)
                startActivity(intent)
            }
            binding.cancelButton.setOnClickListener {
                clearDbDialog.dismiss()
            }
            clearDbDialog.show()
            binding.continueButton.requestFocus()
        }

        binding.tvSettingsSignout.setOnClickListener {
            viewModel.userLogout()
        }
    }

    override fun onDetach() {
        super.onDetach()
        onSettingsSelected = null
    }
}