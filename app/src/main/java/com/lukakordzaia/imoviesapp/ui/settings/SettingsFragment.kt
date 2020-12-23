package com.lukakordzaia.imoviesapp.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var viewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        settings_delete_history.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setMessage("ნამდვილად გსურთ ისტორიის წაშლა?")
                    .setCancelable(false)
                    .setPositiveButton("დიახ") { _, _ ->
                        viewModel.deleteWatchedHistory(requireContext())
                        viewModel.onDeletePressed(requireContext())
                    }
                    .setNegativeButton("არა") { dialog, _ ->
                        dialog.dismiss()
                    }
            val alert = alertDialog.create()
            alert.show()
        }
    }
}