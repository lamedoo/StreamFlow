package com.lukakrodzaia.streamflowtv.ui.settings

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisible
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.baseclasses.activities.BaseFragmentActivity
import com.lukakrodzaia.streamflowtv.databinding.ActivityTvSettingsBinding
import com.lukakrodzaia.streamflowtv.interfaces.OnSettingsSelected
import com.lukakrodzaia.streamflowtv.ui.login.TvProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSettingsActivity: BaseFragmentActivity<ActivityTvSettingsBinding>(), OnSettingsSelected {
    private val profileViewModel: TvProfileViewModel by viewModel()

    override fun getViewBinding() = ActivityTvSettingsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel.getUserData()
    }

    override fun getSettingsType(type: Int) {
        when (type) {
            1 -> {
                binding.infoContainer.setVisible()
                hideViews(listOf(binding.deleteContainer, binding.signOutContainer))
            }
            2 -> {
                binding.deleteContainer.setVisible()
                hideViews(listOf(binding.infoContainer, binding.signOutContainer))
            }
            3 -> {
                binding.signOutContainer.setVisible()
                hideViews(listOf(binding.infoContainer, binding.deleteContainer))

                if (sharedPreferences.getLoginToken() != "") {
                    profileViewModel.userData.observe(this, {
                        binding.profileName.text = getString(R.string.logged_in_as, it.displayName)
                        Glide.with(this).load(it.avatar.large).into(binding.profilePhoto)
                    })
                }

            }
        }
    }

    private fun hideViews(views: List<View>) {
        views.forEach {
            it.setGone()
        }
    }
}