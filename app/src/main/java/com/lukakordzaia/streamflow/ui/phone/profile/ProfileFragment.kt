package com.lukakordzaia.streamflow.ui.phone.profile

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogConnectTraktvAlertBinding
import com.lukakordzaia.streamflow.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneProfileBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.models.trakttv.request.AddNewListRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.request.GetUserTokenRequestBody
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentVM
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setVisibleOrGone
import kotlinx.android.synthetic.main.dialog_connect_traktv_alert.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragmentVM<FragmentPhoneProfileBinding, ProfileViewModel>() {
    override val viewModel by viewModel<ProfileViewModel>()

    private var traktToken: String? = null

    private lateinit var traktDialog: Dialog
    private val countdown = object : CountDownTimer(600000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            traktDialog.expire_timer?.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            traktDialog.expire_timer?.text = "განაახლეთ კოდი"
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneProfileBinding
        get() = FragmentPhoneProfileBinding::inflate

    override fun onStart() {
        super.onStart()

        viewModel.getUserData()
        updateProfileUI(sharedPreferences.getLoginToken() != "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBarListener(resources.getString(R.string.account), binding.toolbar)

        binding.aboutTitle.text = "ვერსია v${requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName}"

        traktDialog = Dialog(requireContext())
        traktToken = sharedPreferences.getTraktToken()

        fragmentListeners()
        fragmentObservers()
    }

    private fun fragmentListeners() {
        binding.gSignIn.setOnClickListener {
            viewModel.onLoginPressed()
        }

        binding.gSignOut.setOnClickListener {
            viewModel.userLogout()
        }

        binding.clearContainer.setOnClickListener {
            val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
            val clearHistory = Dialog(requireContext())
            clearHistory.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearHistory.setContentView(binding.root)

            binding.continueButton.setOnClickListener {
                if (sharedPreferences.getLoginToken() == "") {
                    viewModel.deleteContinueWatchingFromRoomFull()
                }
                clearHistory.dismiss()
            }
            binding.cancelButton.setOnClickListener {
                clearHistory.dismiss()
            }
            clearHistory.show()
        }

        if (traktToken == "") {
            updateTraktUi(false)
        }

        binding.traktContainer.setOnClickListener {
            val binding = DialogConnectTraktvAlertBinding.inflate(LayoutInflater.from(requireContext()))
            traktDialog.setContentView(binding.root)
            traktDialog.show()
            viewModel.getDeviceCode()

            viewModel.traktDeviceCodeResponse.observe(viewLifecycleOwner, {
                binding.userCode.text = it.userCode
                binding.traktvUrl.text = it.verificationUrl
                countdown.start()

                binding.userCode.setOnClickListener { _ ->
                    val clipboard: ClipboardManager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Copied Code", it.userCode)
                    clipboard.setPrimaryClip(clip)
                    requireContext().createToast("კოდი კოპირებულია")
                }

                binding.traktvUrl.setOnClickListener { _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.verificationUrl)
                    startActivity(intent)
                }

                viewModel.getUserToken(
                    GetUserTokenRequestBody(
                        AppConstants.TRAKTV_CLIENT_ID,
                        AppConstants.TRAKTV_CLIENT_SECRET,
                        it.deviceCode
                    )
                )
            })

            binding.cancelButton.setOnClickListener {
                traktDialog.hide()
                countdown.cancel()
            }
        }

//        binding.disconnectTraktv.setOnClickListener {
//            authSharedPreferences.saveAccessToken("")
//            authSharedPreferences.saveRefreshToken("")
//            updateTraktUi(false)
//        }
    }

    private fun fragmentObservers() {
        viewModel.loginLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {}
                LoadingState.Status.SUCCESS -> viewModel.refreshProfileOnLogin()
            }
        })

        viewModel.userUserTokenResponse.observe(viewLifecycleOwner, { userToken ->
            if (userToken != null) {
                traktDialog.hide()
                countdown.cancel()

                sharedPreferences.saveTraktToken(userToken.accessToken)
                sharedPreferences.saveTraktRefreshToken(userToken.refreshToken)

                updateTraktUi(true)
            }
        })

        viewModel.traktSfListExists.observe(viewLifecycleOwner, {
            if (!it) {
                viewModel.createNewList(
                    AddNewListRequestBody(
                        null,
                        null,
                        null,
                        "StreamFlow List",
                        "public",
                        null,
                        null
                    ),
                    "Bearer ${sharedPreferences.getTraktToken()}"
                )
            }
        })
    }

    private fun updateTraktUi(isLoggedIn: Boolean) {
//        if (isLoggedIn) {
//            binding.disconnectTraktv.setVisible()
//            binding.connectTraktv.setGone()
//        } else {
//            binding.disconnectTraktv.setGone()
//            binding.connectTraktv.setVisible()
//        }
    }

    private fun updateProfileUI(isLoggedIn: Boolean) {
        binding.profileContainer.setVisibleOrGone(isLoggedIn)
        binding.gSignIn.setVisibleOrGone(!isLoggedIn)
        binding.gSignOut.setVisibleOrGone(isLoggedIn)
        binding.lastLine.setVisibleOrGone(isLoggedIn)
        binding.firstLine.setVisibleOrGone(!isLoggedIn)
        binding.clearContainer.setVisibleOrGone(!isLoggedIn)
        binding.historyLine.setVisibleOrGone(!isLoggedIn)

        if (isLoggedIn) {
            viewModel.userData.observe(viewLifecycleOwner, {
                binding.profileUsername.text = it.displayName
                Glide.with(this).load(it.avatar.large).into(binding.profilePhoto)
            })
        }
    }
}