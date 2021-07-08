package com.lukakordzaia.streamflow.ui.phone.profile

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflow.databinding.DialogSyncDatabaseBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneProfileBinding
import com.lukakordzaia.streamflow.datamodels.TraktNewList
import com.lukakordzaia.streamflow.datamodels.TraktRequestToken
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.connect_traktv_alert_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment<FragmentPhoneProfileBinding>() {
    private val profileViewModel: ProfileViewModel by viewModel()
    private var googleAccount: GoogleSignInAccount? = null
    private var traktToken: String? = null

    private lateinit var traktDialog: Dialog
    private val countdown = object : CountDownTimer(600000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            traktDialog.connect_traktv_expire_timer?.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            traktDialog.connect_traktv_expire_timer?.text = "განაახლეთ კოდი"
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneProfileBinding
        get() = FragmentPhoneProfileBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true
        }

        googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        traktDialog = Dialog(requireContext())
        traktToken = authSharedPreferences.getAccessToken()

        topBarListener("პროფილი")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.googleSignInButton.setOnClickListener {
            val signInIntent: Intent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.googleSignOutButton.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            updateGoogleUI(null)
        }

        binding.deleteHistory.setOnClickListener {
            val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
            val clearHistory = Dialog(requireContext())
            clearHistory.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearHistory.setContentView(binding.root)

            binding.continueButton.setOnClickListener {
                if (auth.currentUser == null) {
                    profileViewModel.deleteContinueWatchingFromRoomFull(requireContext())
                } else {
                    profileViewModel.deleteContinueWatchingFromFirestoreFull()
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

        binding.connectTraktv.setOnClickListener {
            traktDialog.setContentView(layoutInflater.inflate(R.layout.connect_traktv_alert_dialog,null))
            traktDialog.show()
            profileViewModel.getDeviceCode()

            profileViewModel.traktDeviceCode.observe(viewLifecycleOwner, {
                traktDialog.connect_traktv_user_code.text = it.userCode
                traktDialog.connect_traktv_url.text = it.verificationUrl
                countdown.start()

                traktDialog.connect_traktv_user_code.setOnClickListener { _ ->
                    val clipboard: ClipboardManager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Copied Code", it.userCode)
                    clipboard.setPrimaryClip(clip)
                    requireContext().createToast("კოდი კოპირებულია")
                }

                traktDialog.connect_traktv_url.setOnClickListener { _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.verificationUrl)
                    startActivity(intent)
                }

                profileViewModel.getUserToken(
                    TraktRequestToken(
                        AppConstants.TRAKTV_CLIENT_ID,
                        AppConstants.TRAKTV_CLIENT_SECRET,
                        it.deviceCode
                    )
                )
            })

            traktDialog.connect_traktv_dialog_close.setOnClickListener {
                traktDialog.hide()
                countdown.cancel()
            }
        }

        binding.disconnectTraktv.setOnClickListener {
            authSharedPreferences.saveAccessToken("")
            authSharedPreferences.saveRefreshToken("")
            updateTraktUi(false)
        }


        profileViewModel.traktUserToken.observe(viewLifecycleOwner, { userToken ->
            if (userToken != null) {
                traktDialog.hide()
                countdown.cancel()

                authSharedPreferences.saveAccessToken(userToken.accessToken)
                authSharedPreferences.saveRefreshToken(userToken.refreshToken)

                updateTraktUi(true)
            }
        })

        profileViewModel.traktSfListExists.observe(viewLifecycleOwner, {
            if (!it) {
                profileViewModel.createNewList(
                    TraktNewList(
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

        profileViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        profileViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.idToken)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
//                    updateGoogleUI(user)
                    Snackbar.make(binding.profileRoot,"წარმატებით გაიარეთ ავტორიზაცია", Snackbar.LENGTH_SHORT).show()
                    profileViewModel.createUserFirestore()

                    showSyncDialog()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(binding.profileRoot, "ავტორიზაცია ვერ მოხერხდა", Snackbar.LENGTH_SHORT)
                        .show()
                    updateGoogleUI(null)
                }
            }
    }

    private fun showSyncDialog() {
        profileViewModel.getContinueWatchingFromRoom(requireContext()).observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                val binding = DialogSyncDatabaseBinding.inflate(LayoutInflater.from(requireContext()))
                val syncDialog = Dialog(requireContext())
                syncDialog.setContentView(binding.root)

                binding.confirmButton.setOnClickListener { _ ->
                    profileViewModel.addContinueWatchingToFirestore(requireContext(), it)
                    syncDialog.dismiss()
                }
                binding.cancelButton.setOnClickListener {
                    syncDialog.dismiss()
                }
                syncDialog.show()
            } else {
                profileViewModel.refreshProfileOnLogin()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        updateGoogleUI(currentUser)
    }

    private fun updateTraktUi(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            binding.disconnectTraktv.setVisible()
            binding.connectTraktv.setGone()
        } else {
            binding.disconnectTraktv.setGone()
            binding.connectTraktv.setVisible()
        }

    }

    private fun updateGoogleUI(user: FirebaseUser?) {
        if (user != null) {
            binding.profilePhoto.setVisible()

            if (googleAccount != null) {
                binding.profileUsername.text = "${googleAccount!!.givenName} ${googleAccount!!.familyName}"
                Picasso.get().load(googleAccount!!.photoUrl).into(binding.profilePhoto)
            }

            binding.googleSignInButton.setGone()
            binding.googleSignOutButton.setVisible()
        } else {
            binding.profileUsername.text = ""
            binding.profilePhoto.setGone()

            binding.googleSignInButton.setVisible()
            binding.googleSignOutButton.setGone()
        }
    }

    companion object {

        const val RC_SIGN_IN = 13

    }
}