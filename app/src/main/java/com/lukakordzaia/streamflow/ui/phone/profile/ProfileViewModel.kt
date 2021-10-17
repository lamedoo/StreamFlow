package com.lukakordzaia.streamflow.ui.phone.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserDataResponse
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class ProfileViewModel : BaseViewModel() {
    private val _userData = MutableLiveData<GetUserDataResponse.Data>()
    val userData: LiveData<GetUserDataResponse.Data> = _userData

    fun onLoginPressed() {
        navigateToNewFragment(ProfileFragmentDirections.actionProfileFragmentToLoginBottomSheetFragment())
    }

    fun userLogin(loginBody: PostLoginBody) {
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
                    Log.d("userLogin", login.exception)
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

                    setGeneralLoader(LoadingState.LOADED)
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