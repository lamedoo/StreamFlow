package com.lukakordzaia.streamflow.ui.baseclasses

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileFragment
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.tv.TvActivity
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.sync_continue_watching_alert_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseFragmentActivity : FragmentActivity() {
    private val profileViewModel: ProfileViewModel by viewModel()
    private var googleAccount: GoogleSignInAccount? = null
    private var googleSignInClient: GoogleSignInClient? = null
    protected val auth = Firebase.auth
    private lateinit var signInButton: View
    private lateinit var signOutButton: View
    private lateinit var profilePhoto: ImageView
    private lateinit var profileUsername: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAccount = GoogleSignIn.getLastSignedInAccount(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun googleSignIn(view: View) {
        signInButton = view
        view.setOnClickListener {
            this.createToast("ავტორიზაცია")
            val signInIntent: Intent = googleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, ProfileFragment.RC_SIGN_IN)
        }
    }

    fun googleSignOut(view: View) {
        signOutButton = view
        view.setOnClickListener {
            auth.signOut()
            googleSignInClient!!.signOut()
            updateGoogleUI(null)
            val intent = Intent(this, TvActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(intent)
        }
    }

    fun googleProfileDetails(photo: ImageView, username: TextView) {
        profilePhoto = photo
        profileUsername = username
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ProfileFragment.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.idToken)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                        val user = auth.currentUser
//                        updateGoogleUI(user)
                        this.createToast("წარმატებით გაიარეთ ავტორიზაცია")
                        profileViewModel.createUserFirestore()

                        showSyncDialog()
                    } else {
                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                        updateGoogleUI(null)
                    }
                }
    }

    private fun showSyncDialog() {
        profileViewModel.getContinueWatchingFromRoom(this).observe(this, {
            if (!it.isNullOrEmpty()) {
                val syncDialog = Dialog(this)
                syncDialog.setContentView(layoutInflater.inflate(R.layout.sync_continue_watching_alert_dialog,null))
                syncDialog.sync_continue_watching_alert_yes.setOnClickListener { _ ->
                    profileViewModel.addContinueWatchingToFirestore(this, it)
                        val intent = Intent(this, TvActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        this.startActivity(intent)

                }
                syncDialog.sync_continue_watching_alert_no.setOnClickListener {
                    syncDialog.dismiss()
                }
                syncDialog.show()
            } else {
                val intent = Intent(this, TvActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(intent)
            }
        })
    }

    fun updateGoogleUI(user: FirebaseUser?) {
        if (user != null) {
            profilePhoto.setVisible()

            if (googleAccount != null) {
                profileUsername.text = "${googleAccount!!.givenName} ${googleAccount!!.familyName}"
                Picasso.get().load(googleAccount!!.photoUrl).into(profilePhoto)
            }

            signInButton.setGone()
            signOutButton.setVisible()

            signOutButton.requestFocus()


        } else {
            profileUsername.text = ""
            profilePhoto.setGone()

            signInButton.setVisible()
            signOutButton.setGone()

            signInButton.requestFocus()
        }
    }
}