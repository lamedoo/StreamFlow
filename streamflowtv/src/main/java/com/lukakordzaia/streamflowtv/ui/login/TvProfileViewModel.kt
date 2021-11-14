package com.lukakordzaia.streamflowtv.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserDataResponse
import kotlinx.coroutines.launch

class TvProfileViewModel : BaseViewModel() {
    private val _userData = MutableLiveData<GetUserDataResponse.Data>()
    val userData: LiveData<GetUserDataResponse.Data> = _userData

    fun userLogin(loginBody: PostLoginBody) {
        setNoInternet(false)
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val login = environment.userRepository.userLogin(loginBody)) {
                is Result.Success -> {
                    val data = login.data

                    sharedPreferences.saveLoginToken(data.accessToken)
                    sharedPreferences.saveLoginRefreshToken(data.refreshToken)
                    sharedPreferences.saveUsername(loginBody.username)
                    sharedPreferences.savePassword(loginBody.password)

                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    newToastMessage("დაფიქსირდა გარკვეული შეცდომა, გთხოვთ სცადოთ თავიდან")
                }
                is Result.Internet -> {
                    setNoInternet(true)
                }
            }
        }
    }

    fun userLogout() {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val logout = environment.userRepository.userLogout()) {
                is Result.Success -> {

                    sharedPreferences.saveLoginToken("")
                    sharedPreferences.saveLoginRefreshToken("")
                    sharedPreferences.saveUserId(-1)

                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    Log.d("userLogout", logout.exception)
                }
            }
        }
    }

    fun getUserData() {
        setNoInternet(false)
        viewModelScope.launch {
            when (val userData = environment.userRepository.userData()) {
                is Result.Success -> {
                    val data = userData.data.data

                    sharedPreferences.saveUserId(data.id)

                    _userData.value = data
                }
                is Result.Error -> {
                    Log.d("userData", userData.exception)
                }
                is Result.Internet -> {
                    setNoInternet(true)
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