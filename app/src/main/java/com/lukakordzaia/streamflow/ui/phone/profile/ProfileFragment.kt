package com.lukakordzaia.streamflow.ui.phone.profile

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.TraktNewList
import com.lukakordzaia.streamflow.datamodels.TraktRequestToken
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setInvisible
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.connect_traktv_alert_dialog.*
import kotlinx.android.synthetic.main.phone_profile_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment(R.layout.phone_profile_framgent) {
    private val profileViewModel: ProfileViewModel by viewModel()
    private lateinit var traktDialog: Dialog
    private val countdown = object : CountDownTimer(600000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            traktDialog.connect_traktv_expire_timer?.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            traktDialog.connect_traktv_expire_timer?.text = "განაახლეთ კოდი"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        traktDialog = Dialog(requireContext())

        if (!authSharedPreferences.getAccessToken().isNullOrBlank()) {
            profile_connect_traktv.isClickable = false
            profile_connect_traktv_title.text = "TRAKT.TV დაკავშირებულია"
            profile_connect_traktv_title.setTextColor(resources.getColor(R.color.green_dark))
            profile_connect_traktv.setOnClickListener {
                requireContext().createToast("თქვენ უკვე დაუკავშირდით პლატფორმას")
            }
            profileViewModel.getUserProfile("Bearer ${authSharedPreferences.getAccessToken()}")

            profile_logout_button.setVisible()
            profile_logout_button.setOnClickListener {
                authSharedPreferences.saveAccessToken("")
                authSharedPreferences.saveRefreshToken("")
                profile_logout_button.setInvisible()
                profile_username.setInvisible()
                profile_connect_traktv_title.text = "დაუკავშირდით TRAKT.TV-ს"
                profile_connect_traktv_title.setTextColor(resources.getColor(R.color.white))
            }
        }

        profile_connect_traktv.setOnClickListener {
            traktDialog.setContentView(layoutInflater.inflate(R.layout.connect_traktv_alert_dialog, null))
            traktDialog.show()
            profileViewModel.getDeviceCode()

            profileViewModel.traktDeviceCode.observe(viewLifecycleOwner, {
                traktDialog.connect_traktv_user_code.text = it.userCode
                traktDialog.connect_traktv_url.text = it.verificationUrl
                countdown.start()

                traktDialog.connect_traktv_user_code.setOnClickListener { _ ->
                    val clipboard: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Copied Code", it.userCode)
                    clipboard.setPrimaryClip(clip)
                    requireContext().createToast("კოდი კოპირებულია")
                }

                traktDialog.connect_traktv_url.setOnClickListener { _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.verificationUrl)
                    startActivity(intent)
                }

                profileViewModel.getUserToken(TraktRequestToken(
                        AppConstants.TRAKTV_CLIENT_ID,
                        AppConstants.TRAKTV_CLIENT_SECRET,
                        it.deviceCode
                ))
            })

            traktDialog.connect_traktv_dialog_close.setOnClickListener {
                traktDialog.hide()
                countdown.cancel()
            }
        }

        profileViewModel.traktUserToken.observe(viewLifecycleOwner, { userToken ->
            if (userToken.accessToken.isNotEmpty()) {
                traktDialog.hide()
                profile_connect_traktv_title.text = "TRAK.TV დაკავშირებულია"
                profile_connect_traktv_title.setTextColor(resources.getColor(R.color.green_dark))
                profile_logout_button.setVisible()
                profile_connect_traktv.isClickable = false
                profile_delete_history.isClickable = true
                countdown.cancel()

                authSharedPreferences.saveAccessToken(userToken.accessToken)
                authSharedPreferences.saveRefreshToken(userToken.refreshToken)

                profileViewModel.traktUserProfile.observe(viewLifecycleOwner, {
                    profile_username.text = it.name
                })
            }
        })

        profileViewModel.traktSfListExists.observe(viewLifecycleOwner, {
            if (!it) {
                profileViewModel.createNewList(TraktNewList(
                        null,
                        null,
                        null,
                        "StreamFlow List",
                        "public",
                        null,
                        null
                ),
                        "Bearer ${authSharedPreferences.getAccessToken()}"
                )
            }
        })

        profile_delete_history.setOnClickListener {
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
            clearDbDialog.clear_db_alert_yes.setOnClickListener {
                profileViewModel.deleteWatchedHistory(requireContext())
                profileViewModel.onDeletePressedPhone(requireContext())
            }
            clearDbDialog.clear_db_alert_no.setOnClickListener {
                clearDbDialog.dismiss()
            }
            clearDbDialog.show()
        }
    }
}