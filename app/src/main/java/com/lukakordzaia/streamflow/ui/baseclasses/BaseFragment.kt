package com.lukakordzaia.streamflow.ui.baseclasses

import androidx.fragment.app.Fragment
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import org.koin.android.ext.android.inject

open class BaseFragment(fragment: Int) : Fragment(fragment) {
    protected val authSharedPreferences: AuthSharedPreferences by inject()
}