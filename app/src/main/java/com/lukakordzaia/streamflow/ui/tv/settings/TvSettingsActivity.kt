package com.lukakordzaia.streamflow.ui.tv.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.lukakordzaia.streamflow.databinding.ActivityTvSettingsBinding
import com.lukakordzaia.streamflow.interfaces.OnSettingsSelected
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso

class TvSettingsActivity: FragmentActivity(), OnSettingsSelected {
    private lateinit var binding: ActivityTvSettingsBinding
    private var googleAccount: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleAccount = GoogleSignIn.getLastSignedInAccount(this)
    }

    override fun getSettingsType(type: Int) {
        when (type) {
            0 -> {
                binding.traktContainer.setVisible()
                hideViews(listOf(binding.infoContainer, binding.deleteContainer, binding.signOutContainer))
            }
            1 -> {
                binding.infoContainer.setVisible()
                hideViews(listOf(binding.traktContainer, binding.deleteContainer, binding.signOutContainer))
            }
            2 -> {
                binding.deleteContainer.setVisible()
                hideViews(listOf(binding.traktContainer, binding.infoContainer, binding.signOutContainer))
            }
            3 -> {
                binding.signOutContainer.setVisible()
                hideViews(listOf(binding.traktContainer, binding.infoContainer, binding.deleteContainer))

                if (googleAccount != null) {
                    binding.profileName.text = "შესული ხართ, როგორც ${googleAccount!!.givenName!!} ${googleAccount!!.familyName!!}"
                    binding.profileEmail.text = "ელ-ფოსტა: ${googleAccount!!.email}"
                    Picasso.get().load(googleAccount!!.photoUrl).into(binding.profilePhoto)
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