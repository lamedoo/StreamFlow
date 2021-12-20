package com.lukakordzaia.streamflowphone.helpers

import androidx.navigation.NavDirections
import com.lukakordzaia.streamflowphone.ui.home.HomeFragmentDirections

class NavigationHelper {
    fun homeToProfile(): NavDirections = HomeFragmentDirections.actionHomeFragmentToProfileFragmentNav()
}
