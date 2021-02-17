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
import androidx.fragment.app.Fragment
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.TraktvRequestToken
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.connect_traktv_alert_dialog.*
import kotlinx.android.synthetic.main.phone_profile_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment(R.layout.phone_profile_framgent) {
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countdown = object : CountDownTimer(600000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                connect_traktv_expire_timer?.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                connect_traktv_expire_timer?.text = "განაახლეთ კოდი"
            }
        }

        profile_connect_traktv.setOnClickListener {
            profileViewModel.getDeviceCode()
            connect_traktv_dialog.setVisible()
            connect_traktv_dialog.isFocusable = true
            profile_connect_traktv.isClickable = false
            profile_delete_history.isClickable = false
        }

        profileViewModel.traktvDeviceCode.observe(viewLifecycleOwner, {
            connect_traktv_user_code.text = it.userCode
            connect_traktv_url.text = it.verificationUrl
            countdown.start()

            connect_traktv_user_code.setOnClickListener {
                val clipboard: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Code", connect_traktv_user_code.text)
                clipboard.setPrimaryClip(clip)
                requireContext().createToast("კოდი კოპირებულია")
            }

            connect_traktv_url.setOnClickListener { _ ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(it.verificationUrl)
                startActivity(intent)
            }

            profileViewModel.getUserToken(TraktvRequestToken(
                    AppConstants.TRAKTV_CLIENT_ID,
                    AppConstants.TRAKTV_CLIENT_SECRET,
                    it.deviceCode
            ))
        })

        connect_traktv_dialog_close.setOnClickListener {
            connect_traktv_dialog.setGone()
            connect_traktv_dialog.isFocusable = false
            profile_connect_traktv.isClickable = true
            profile_delete_history.isClickable = true
            countdown.cancel()
        }

        profileViewModel.traktvUserToken.observe(viewLifecycleOwner, {
            if (it.accessToken.isNotEmpty()) {
                connect_traktv_dialog.setGone()
                profile_connect_traktv.text = "TRAK.TV დაკავშირებულია"
                profile_delete_history.isClickable = true
                countdown.cancel()
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