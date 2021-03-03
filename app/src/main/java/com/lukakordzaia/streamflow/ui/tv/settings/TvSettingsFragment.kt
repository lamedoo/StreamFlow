package com.lukakordzaia.streamflow.ui.tv.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.utils.createToast
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.tv_settings_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSettingsFragment : BaseFragment(R.layout.tv_settings_fragment) {
    private val profileViewModel: ProfileViewModel by viewModel()
    private var googleSignInClient: GoogleSignInClient? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSettingsSelected = context as? OnSettingsSelected
    }

    override fun onDetach() {
        super.onDetach()
        onSettingsSelected = null
    }

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

        tv_settings_trakt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(0)
            }
        }
        tv_settings_info.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(1)
            }
        }
        tv_settings_delete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(2)
            }
        }
        tv_settings_signout.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onSettingsSelected?.getSettingsType(3)
            }
        }

        tv_settings_trakt.setOnClickListener {
            requireContext().createToast("ფუნქცია მალე დაემატება")
        }
        tv_settings_info.setOnClickListener {
            requireContext().createToast("ინფორმაცია აპლიკაციის შესახებ")
        }
        tv_settings_delete.setOnClickListener {
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
            clearDbDialog.clear_db_alert_yes.setOnClickListener {
                profileViewModel.deleteContinueWatchingFromRoomFull(requireContext())
                profileViewModel.deleteContinueWatchingFromFirestoreFull()

                val intent = Intent(requireContext(), TvSettingsActivity::class.java)
                startActivity(intent)
            }
            clearDbDialog.clear_db_alert_no.setOnClickListener {
                clearDbDialog.dismiss()
            }
            clearDbDialog.show()
            clearDbDialog.clear_db_alert_yes.requestFocus()
        }
        tv_settings_signout.setOnClickListener {
            auth.signOut()
            googleSignInClient!!.signOut()
            val intent = Intent(requireContext(), TvSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    interface OnSettingsSelected {
        fun getSettingsType(type: Int)
    }
    var onSettingsSelected: OnSettingsSelected? = null
}