package com.lukakordzaia.streamflowtv.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvProfileViewModel : BaseViewModel() {
    val loginLoader = MutableLiveData(LoadingState.LOADED)

    private val _userData = MutableLiveData<GetUserDataResponse.Data>()
    val userData: LiveData<GetUserDataResponse.Data> = _userData

    fun userLogin(loginBody: PostLoginBody) {
        loginLoader.value = LoadingState.LOADING
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val login = environment.userRepository.userLogin(loginBody)) {
                is ResultData.Success -> {
                    val data = login.data

                    sharedPreferences.saveLoginToken(data.accessToken)
                    sharedPreferences.saveLoginRefreshToken(data.refreshToken)
                    sharedPreferences.saveUsername(loginBody.username)
                    sharedPreferences.savePassword(loginBody.password)

                    loginLoader.value = LoadingState.LOADED
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    newToastMessage("დაფიქსირდა გარკვეული შეცდომა, გთხოვთ სცადოთ თავიდან")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun userLogout() {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val logout = environment.userRepository.userLogout()) {
                is ResultData.Success -> {

                    sharedPreferences.saveLoginToken("")
                    sharedPreferences.saveLoginRefreshToken("")
                    sharedPreferences.saveUserId(-1)

                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    Log.d("userLogout", logout.exception)
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            when (val userData = environment.userRepository.userData()) {
                is ResultData.Success -> {
                    val data = userData.data.data

                    sharedPreferences.saveUserId(data.id)

                    _userData.value = data
                }
                is ResultData.Error -> {
                    Log.d("userData", userData.exception)
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun deleteContinueWatchingFromRoomFull() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                environment.databaseRepository.deleteAllFromRoom()
            }
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