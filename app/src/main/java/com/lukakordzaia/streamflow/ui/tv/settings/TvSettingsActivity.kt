package com.lukakordzaia.streamflow.ui.tv.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tv_settings.*
import kotlinx.android.synthetic.main.fragment_tv_settings.*

class TvSettingsActivity: FragmentActivity(), TvSettingsFragment.OnSettingsSelected {
    private var googleAccount: GoogleSignInAccount? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_settings)

        googleAccount = GoogleSignIn.getLastSignedInAccount(this)
    }

    override fun getSettingsType(type: Int) {
        when (type) {
            0 -> {
                tv_settings_trakt_container.setVisible()
                tv_settings_info_container.setGone()
                tv_settings_delete_container.setGone()
                tv_settings_signout_container.setGone()
            }
            1 -> {
                tv_settings_info_container.setVisible()
                tv_settings_trakt_container.setGone()
                tv_settings_delete_container.setGone()
                tv_settings_signout_container.setGone()
            }
            2 -> {
                tv_settings_delete_container.setVisible()
                tv_settings_trakt_container.setGone()
                tv_settings_info_container.setGone()
                tv_settings_signout_container.setGone()
            }
            3 -> {
                tv_settings_signout_container.setVisible()
                tv_settings_delete_container.setGone()
                tv_settings_trakt_container.setGone()
                tv_settings_info_container.setGone()

                if (googleAccount != null) {
                    tv_settings_profile_name.text = "შესული ხართ, როგორც ${googleAccount!!.givenName!!} ${googleAccount!!.familyName!!}"
                    tv_settings_profile_email.text = "ელ-ფოსტა: ${googleAccount!!.email}"
                    Picasso.get().load(googleAccount!!.photoUrl).into(tv_settings_profile_photo)
                }

            }
        }
    }
}