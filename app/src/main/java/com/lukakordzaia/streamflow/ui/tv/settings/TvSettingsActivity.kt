package com.lukakordzaia.streamflow.ui.tv.settings

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.databinding.ActivityTvSettingsBinding
import com.lukakordzaia.streamflow.interfaces.OnSettingsSelected
import com.lukakordzaia.streamflow.ui.baseclasses.activities.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSettingsActivity: BaseFragmentActivity<ActivityTvSettingsBinding>(), OnSettingsSelected {
    private val profileViewModel: ProfileViewModel by viewModel()

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
                        binding.profileName.text = "შესული ხართ როგორც ${it.displayName}"
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