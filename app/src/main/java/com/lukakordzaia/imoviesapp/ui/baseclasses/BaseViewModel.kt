package com.lukakordzaia.imoviesapp.ui.baseclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.lukakordzaia.imoviesapp.utils.Event

abstract class BaseViewModel : ViewModel() {
    private val _navigateScreen = MutableLiveData<Event<NavDirections>>()
    private val _toastMessage = MutableLiveData<Event<String>>()

    val navigateScreen: LiveData<Event<NavDirections>> = _navigateScreen
    val toastMessage: LiveData<Event<String>> = _toastMessage

    fun navigateToNewFragment(navId: NavDirections) {
        _navigateScreen.value = Event(navId)
    }

    fun newToastMessage(message: String) {
        _toastMessage.value = Event(message)
    }
}