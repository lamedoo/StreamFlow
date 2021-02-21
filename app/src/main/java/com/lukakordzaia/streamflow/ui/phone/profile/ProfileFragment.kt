package com.lukakordzaia.streamflow.ui.phone.profile

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.phone_profile_framgent.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment(R.layout.phone_profile_framgent) {
    private val profileViewModel: ProfileViewModel by viewModel()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            Firebase.auth.signOut()
            googleSignInClient.signOut()
            updateUI(null)
        }

        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (googleAccount != null) {
            Log.d(TAG, "${googleAccount.givenName} ${googleAccount.familyName}")
        }
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
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(profile_root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
//            binding.status.text = getString(R.string.google_status_fmt, user.email)
//            binding.detail.text = getString(R.string.firebase_status_fmt, user.uid)

            google_sign_in_button.setGone()
            google_sign_out_button.setVisible()


        } else {
//            binding.status.setText(R.string.signed_out)
//            binding.detail.text = null

            google_sign_in_button.setVisible()
            google_sign_out_button.setGone()
        }
    }

    companion object {

        const val RC_SIGN_IN = 13

    }

//    private lateinit var traktDialog: Dialog
//    private val countdown = object : CountDownTimer(600000, 1000) {
//        override fun onTick(millisUntilFinished: Long) {
//            traktDialog.connect_traktv_expire_timer?.text = (millisUntilFinished / 1000).toString()
//        }
//
//        override fun onFinish() {
//            traktDialog.connect_traktv_expire_timer?.text = "განაახლეთ კოდი"
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        traktDialog = Dialog(requireContext())
//
//        if (!authSharedPreferences.getAccessToken().isNullOrBlank()) {
//            profile_connect_traktv.isClickable = false
//            profile_connect_traktv_title.text = "TRAKT.TV დაკავშირებულია"
//            profile_connect_traktv_title.setTextColor(resources.getColor(R.color.green_dark))
//            profile_connect_traktv.setOnClickListener {
//                requireContext().createToast("თქვენ უკვე დაუკავშირდით პლატფორმას")
//            }
//            profileViewModel.getUserProfile("Bearer ${authSharedPreferences.getAccessToken()}")
//
//            profile_logout_button.setVisible()
//            profile_logout_button.setOnClickListener {
//                authSharedPreferences.saveAccessToken("")
//                authSharedPreferences.saveRefreshToken("")
//                profile_logout_button.setInvisible()
//                profile_username.setInvisible()
//                profile_connect_traktv_title.text = "დაუკავშირდით TRAKT.TV-ს"
//                profile_connect_traktv_title.setTextColor(resources.getColor(R.color.white))
//            }
//        }
//
//        profile_connect_traktv.setOnClickListener {
//            traktDialog.setContentView(layoutInflater.inflate(R.layout.connect_traktv_alert_dialog, null))
//            traktDialog.show()
//            profileViewModel.getDeviceCode()
//
//            profileViewModel.traktDeviceCode.observe(viewLifecycleOwner, {
//                traktDialog.connect_traktv_user_code.text = it.userCode
//                traktDialog.connect_traktv_url.text = it.verificationUrl
//                countdown.start()
//
//                traktDialog.connect_traktv_user_code.setOnClickListener { _ ->
//                    val clipboard: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                    val clip = ClipData.newPlainText("Copied Code", it.userCode)
//                    clipboard.setPrimaryClip(clip)
//                    requireContext().createToast("კოდი კოპირებულია")
//                }
//
//                traktDialog.connect_traktv_url.setOnClickListener { _ ->
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = Uri.parse(it.verificationUrl)
//                    startActivity(intent)
//                }
//
//                profileViewModel.getUserToken(TraktRequestToken(
//                        AppConstants.TRAKTV_CLIENT_ID,
//                        AppConstants.TRAKTV_CLIENT_SECRET,
//                        it.deviceCode
//                ))
//            })
//
//            traktDialog.connect_traktv_dialog_close.setOnClickListener {
//                traktDialog.hide()
//                countdown.cancel()
//            }
//        }
//
//        profileViewModel.traktUserToken.observe(viewLifecycleOwner, { userToken ->
//            if (userToken.accessToken.isNotEmpty()) {
//                traktDialog.hide()
//                profile_connect_traktv_title.text = "TRAK.TV დაკავშირებულია"
//                profile_connect_traktv_title.setTextColor(resources.getColor(R.color.green_dark))
//                profile_logout_button.setVisible()
//                profile_connect_traktv.isClickable = false
//                profile_delete_history.isClickable = true
//                countdown.cancel()
//
//                authSharedPreferences.saveAccessToken(userToken.accessToken)
//                authSharedPreferences.saveRefreshToken(userToken.refreshToken)
//
//                profileViewModel.traktUserProfile.observe(viewLifecycleOwner, {
//                    profile_username.text = it.name
//                })
//            }
//        })
//
//        profileViewModel.traktSfListExists.observe(viewLifecycleOwner, {
//            if (!it) {
//                profileViewModel.createNewList(TraktNewList(
//                        null,
//                        null,
//                        null,
//                        "StreamFlow List",
//                        "public",
//                        null,
//                        null
//                ),
//                        "Bearer ${authSharedPreferences.getAccessToken()}"
//                )
//            }
//        })
//
//        profile_delete_history.setOnClickListener {
//            val clearDbDialog = Dialog(requireContext())
//            clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
//            clearDbDialog.clear_db_alert_yes.setOnClickListener {
//                profileViewModel.deleteWatchedHistory(requireContext())
//                profileViewModel.onDeletePressedPhone(requireContext())
//            }
//            clearDbDialog.clear_db_alert_no.setOnClickListener {
//                clearDbDialog.dismiss()
//            }
//            clearDbDialog.show()
//        }
//    }
}