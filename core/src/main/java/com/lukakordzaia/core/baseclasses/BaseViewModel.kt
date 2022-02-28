package com.lukakordzaia.core.baseclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.sharedpreferences.SharedPreferences
import com.lukakordzaia.core.utils.Event
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {
    protected val sharedPreferences: SharedPreferences by inject()

    private val _navigateScreen = MutableLiveData<Event<NavDirections>>()
    val navigateScreen: LiveData<Event<NavDirections>> = _navigateScreen

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> = _toastMessage

    private val _noInternet = MutableLiveData<Boolean>()
    val noInternet: LiveData<Boolean> = _noInternet

    private val _generalLoader = MutableLiveData<LoadingState>()
    val generalLoader: LiveData<LoadingState> = _generalLoader

    fun navigateToNewFragment(navId: NavDirections) {
        _navigateScreen.value = Event(navId)
    }

    fun newToastMessage(message: String) {
        _toastMessage.postValue(Event(message))
    }

    fun setNoInternet(internet: Boolean = true) {
        _noInternet.postValue(internet)
    }

    fun setGeneralLoader(loading: LoadingState) {
        _generalLoader.postValue(loading)
    }
}