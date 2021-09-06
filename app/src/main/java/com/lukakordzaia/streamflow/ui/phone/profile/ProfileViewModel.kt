package com.lukakordzaia.streamflow.ui.phone.profile

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserDataResponse
import com.lukakordzaia.streamflow.network.models.trakttv.request.AddNewListRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.request.GetUserTokenRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.response.GetDeviceCodeResponse
import com.lukakordzaia.streamflow.network.models.trakttv.response.GetUserTokenResponse
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.phone.home.HomeFragmentDirections
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class ProfileViewModel : BaseViewModel() {
    val loginLoader = MutableLiveData<LoadingState>()

    private val _userData = MutableLiveData<GetUserDataResponse.Data>()
    val userData: LiveData<GetUserDataResponse.Data> = _userData

    private val _traktDeviceCode = MutableLiveData<GetDeviceCodeResponse>()
    val traktDeviceCodeResponse: LiveData<GetDeviceCodeResponse> = _traktDeviceCode

    private var counter: Long = 0
    private val validationCounter = MutableLiveData<Long>(0)

    private val _traktUserToken = MutableLiveData<GetUserTokenResponse>(null)
    val userUserTokenResponse: LiveData<GetUserTokenResponse> = _traktUserToken

    private val _traktSfListExists = MutableLiveData<Boolean>()
    val traktSfListExists: LiveData<Boolean> = _traktSfListExists

    fun onLoginPressed() {
        navigateToNewFragment(ProfileFragmentDirections.actionProfileFragmentToLoginBottomSheetFragment())
    }

    fun userLogin(loginBody: PostLoginBody) {
        loginLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val login = environment.userRepository.userLogin(loginBody)) {
                is Result.Success -> {
                    val data = login.data

                    sharedPreferences.saveLoginToken(data.accessToken)
                    sharedPreferences.saveLoginRefreshToken(data.refreshToken)
                    sharedPreferences.saveUsername(loginBody.username)
                    sharedPreferences.savePassword(loginBody.password)

                    loginLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    Log.d("userLogin", login.exception)
                }
            }
        }
    }

    fun userLogout() {
        loginLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val logout = environment.userRepository.userLogout()) {
                is Result.Success -> {

                    sharedPreferences.saveLoginToken("")
                    sharedPreferences.saveLoginRefreshToken("")

                    loginLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    Log.d("userLogout", logout.exception)
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            when (val userData = environment.userRepository.userData()) {
                is Result.Success -> {
                    val data = userData.data.data

                    _userData.value = data
                }
                is Result.Error -> {
                    Log.d("userData", userData.exception)
                }
            }
        }
    }

    fun refreshProfileOnLogin() {
        navigateToNewFragment(ProfileFragmentDirections.actionProfileFragmentSelf())
    }

    fun getDeviceCode() {
        viewModelScope.launch {
            when (val deviceCode = environment.traktRepository.getDeviceCode()) {
                is Result.Success -> {
                    val data = deviceCode.data

                    _traktDeviceCode.value = data
                }
            }
        }
    }

    fun getUserToken(tokenRequestRequestBody: GetUserTokenRequestBody) {
        viewModelScope.launch {
            when (val token = environment.traktRepository.getUserToken(tokenRequestRequestBody)) {
                is Result.Success -> {
                    val data = token.data

                    _traktUserToken.value = data

                    getSfList("Bearer ${data.accessToken}")
                }
                is Result.Error -> {
                    when (token.exception) {
                        AppConstants.TRAKT_PENDING_AUTH -> {
                            if (validationCounter.value!! < "${traktDeviceCodeResponse.value!!.expiresIn}00".toLong()) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    getUserToken(tokenRequestRequestBody)
                                }, 5000)

                                counter += 5000
                                validationCounter.value = counter
                            }
                        }
                    }
                }
            }
        }
    }

    fun createNewList(newList: AddNewListRequestBody, accessToken: String) {
        viewModelScope.launch {
            when (val list = environment.traktRepository.createNewList(newList, accessToken)) {
                is Result.Success -> {
                    Log.d("traktvlist", "წარმატებულია")
                }
            }
        }
    }

    private fun getSfList(accessToken: String) {
        viewModelScope.launch {
            when (val sfList = environment.traktRepository.getSfList(accessToken)) {
                is Result.Success -> {
                    _traktSfListExists.value = true
                    Log.d("traktvlist", sfList.data.toString())
                }
                is Result.Error -> {
                    when (sfList.exception) {
                        AppConstants.TRAKT_NOT_FOUND -> {
                            _traktSfListExists.value = false
                        }
                    }
                }
            }
        }
    }

    fun deleteContinueWatchingFromRoomFull() {
        viewModelScope.launch {
            environment.databaseRepository.deleteAllFromRoom()
        }
    }

    fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>> {
        return environment.databaseRepository.getContinueWatchingFromRoom()
    }

    fun addContinueWatchingToApi(continueWatchingRoomList: List<ContinueWatchingRoom>) {
        viewModelScope.launch {
            continueWatchingRoomList.forEach {
//                val addToFirestore = environment.databaseRepository.addContinueWatchingTitleToFirestore(currentUser()!!.uid, it)
//                if (addToFirestore) {
//                    newToastMessage("სინქრონიზაცია წარმატებით დასრულდა")
//                    deleteContinueWatchingFromRoomFull()
//                    refreshProfileOnLogin()
//                } else {
//                    newToastMessage("სამწუხაროდ, ვერ მოხერხდა სინქრონიზაცია")
//                }
            }
        }
    }
}