package com.lukakordzaia.streamflow.ui.tv.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflow.databinding.FragmentTvSettingsBinding
import com.lukakordzaia.streamflow.interfaces.OnSettingsSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setGone
import kotlinx.android.synthetic.main.fragment_tv_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSettingsFragment : BaseFragment<FragmentTvSettingsBinding>() {
    private val profileViewModel: ProfileViewModel by viewModel()
    private var googleSignInClient: GoogleSignInClient? = null
    private var onSettingsSelected: OnSettingsSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSettingsSelected = context as? OnSettingsSelected
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvSettingsBinding
        get() = FragmentTvSettingsBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true
        }

        authCheck()
        fragmentFocusListeners()
        fragmentClickListeners()
    }

    private fun authCheck() {
        if (auth.currentUser == null) {
            binding.tvSettingsSignout.setGone()
        }
    }

    private fun fragmentFocusListeners() {
        binding.tvSettingsTrakt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(0)
            }
        }
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
        binding.tvSettingsTrakt.setOnClickListener {
            requireContext().createToast("ფუნქცია მალე დაემატება")
        }

        binding.tvSettingsInfo.setOnClickListener {
            requireContext().createToast("ინფორმაცია აპლიკაციის შესახებ")
        }

        binding.tvSettingsDelete.setOnClickListener {
            val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.setContentView(binding.root)

            binding.continueButton.setOnClickListener {
                profileViewModel.deleteContinueWatchingFromRoomFull(requireContext())
                profileViewModel.deleteContinueWatchingFromFirestoreFull()

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
            auth.signOut()
            googleSignInClient!!.signOut()
            val intent = Intent(requireContext(), TvSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDetach() {
        super.onDetach()
        onSettingsSelected = null
    }
}