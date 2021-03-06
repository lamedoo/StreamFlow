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
import com.lukakordzaia.streamflow.datamodels.TraktNewList
import com.lukakordzaia.streamflow.datamodels.TraktRequestToken
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.connect_traktv_alert_dialog.*
import kotlinx.android.synthetic.main.phone_profile_framgent.*
import kotlinx.android.synthetic.main.sync_continue_watching_alert_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment() {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.phone_profile_framgent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            Log.d("onviewcreated", "true")
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

        google_sign_in_button.setOnClickListener {
            val signInIntent: Intent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        google_sign_out_button.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            updateGoogleUI(null)
        }

        profile_delete_history.setOnClickListener {
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog,null))
            clearDbDialog.clear_db_alert_yes.setOnClickListener {
                if (auth.currentUser == null) {
                    profileViewModel.deleteContinueWatchingFromRoomFull(requireContext())
                } else {
                    profileViewModel.deleteContinueWatchingFromFirestoreFull()
                }
                clearDbDialog.dismiss()
            }
            clearDbDialog.clear_db_alert_no.setOnClickListener {
                clearDbDialog.dismiss()
            }
            clearDbDialog.show()
        }

        if (traktToken == "") {
            updateTraktUi(false)
        }

        profile_connect_traktv.setOnClickListener {
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

        profile_disconnect_traktv.setOnClickListener {
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
                    Snackbar.make(profile_root,"წარმატებით გაიარეთ ავტორიზაცია", Snackbar.LENGTH_SHORT).show()
                    profileViewModel.createUserFirestore()

                    showSyncDialog()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(profile_root, "ავტორიზაცია ვერ მოხერხდა", Snackbar.LENGTH_SHORT)
                        .show()
                    updateGoogleUI(null)
                }
            }
    }

    private fun showSyncDialog() {
        profileViewModel.getContinueWatchingFromRoom(requireContext()).observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                val syncDialog = Dialog(requireContext())
                syncDialog.setContentView(layoutInflater.inflate(R.layout.sync_continue_watching_alert_dialog,null))
                syncDialog.sync_continue_watching_alert_yes.setOnClickListener { _ ->
                    profileViewModel.addContinueWatchingToFirestore(requireContext(), it)
                    syncDialog.dismiss()
                }
                syncDialog.sync_continue_watching_alert_no.setOnClickListener {
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
            profile_disconnect_traktv.setVisible()
            profile_connect_traktv.setGone()
        } else {
            profile_disconnect_traktv.setGone()
            profile_connect_traktv.setVisible()
        }

    }

    private fun updateGoogleUI(user: FirebaseUser?) {
        if (user != null) {
            profile_photo.setVisible()

            if (googleAccount != null) {
                profile_username.text = "${googleAccount!!.givenName} ${googleAccount!!.familyName}"
                Picasso.get().load(googleAccount!!.photoUrl).into(profile_photo)
            }

            google_sign_in_button.setGone()
            google_sign_out_button.setVisible()


        } else {
            profile_username.text = ""
            profile_photo.setGone()

            google_sign_in_button.setVisible()
            google_sign_out_button.setGone()
        }
    }

    companion object {

        const val RC_SIGN_IN = 13

    }
}