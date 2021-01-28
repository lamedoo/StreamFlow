package com.lukakordzaia.imoviesapp.ui.phone.settings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var viewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        settings_delete_history.setOnClickListener {
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
            clearDbDialog.clear_db_alert_yes.setOnClickListener {
                        viewModel.deleteWatchedHistory(requireContext())
                        viewModel.onDeletePressed(requireContext())
                    }
            clearDbDialog.clear_db_alert_no.setOnClickListener {
                clearDbDialog.dismiss()
            }
            clearDbDialog.show()
        }
    }
}