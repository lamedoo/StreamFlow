package com.lukakordzaia.streamflow.ui.tv.settings

import androidx.fragment.app.Fragment
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSettingsFragment : Fragment(R.layout.tv_settings_fragment) {
    private val profileViewModel: ProfileViewModel by viewModel()
}