package com.lukakordzaia.streamflow.ui.phone.settings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.connect_traktv_alert_dialog.*
import kotlinx.android.synthetic.main.phone_settings_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.phone_settings_framgent) {
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        settings_connect_traktv.setOnClickListener {
            settingsViewModel.getDeviceCode()
            connect_traktv_dialog.setVisible()
            connect_traktv_dialog.isFocusable = true
            settings_connect_traktv.isClickable = false
            settings_delete_history.isClickable = false
        }

        settingsViewModel.traktvDeviceCode.observe(viewLifecycleOwner, {
            connect_traktv_user_code.text = it.userCode
            connect_traktv_url.text = it.verificationUrl
        })

        settings_delete_history.setOnClickListener {
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
            clearDbDialog.clear_db_alert_yes.setOnClickListener {
                settingsViewModel.deleteWatchedHistory(requireContext())
                settingsViewModel.onDeletePressedPhone(requireContext())
            }
            clearDbDialog.clear_db_alert_no.setOnClickListener {
                clearDbDialog.dismiss()
            }
            clearDbDialog.show()
        }
    }
}