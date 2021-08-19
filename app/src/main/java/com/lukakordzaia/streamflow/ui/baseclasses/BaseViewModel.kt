package com.lukakordzaia.streamflow.ui.baseclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.helpers.Environment
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import com.lukakordzaia.streamflow.utils.Event
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {
    protected val authSharedPreferences: AuthSharedPreferences by inject()
    protected val environment: Environment by inject()

    private val _navigateScreen = MutableLiveData<Event<NavDirections>>()
    val navigateScreen: LiveData<Event<NavDirections>> = _navigateScreen

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> = _toastMessage

    private val _noInternet = MutableLiveData(Event(false))
    val noInternet: LiveData<Event<Boolean>> = _noInternet


    fun navigateToNewFragment(navId: NavDirections) {
        _navigateScreen.value = Event(navId)
    }

    fun newToastMessage(message: String) {
        _toastMessage.value = Event(message)
    }

    fun setNoInternet() {
        _noInternet.value = Event(true)
    }

    fun currentUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }
}