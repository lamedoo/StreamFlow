package com.lukakordzaia.streamflowphone.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.usecases.DbDeleteAllContinueWatchingUseCase
import com.lukakordzaia.core.domain.usecases.UserDataUseCase
import com.lukakordzaia.core.domain.usecases.UserLogOutUseCase
import com.lukakordzaia.core.domain.usecases.UserLoginUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.request.user.PostLoginBody
import com.lukakordzaia.core.network.models.imovies.response.user.GetUserDataResponse
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userLoginUseCase: UserLoginUseCase,
    private val userLogOutUseCase: UserLogOutUseCase,
    private val userDataUseCase: UserDataUseCase,
    private val dbDeleteAllContinueWatchingUseCase: DbDeleteAllContinueWatchingUseCase
) : BaseViewModel() {
    private val _userData = MutableLiveData<GetUserDataResponse.Data>()
    val userData: LiveData<GetUserDataResponse.Data> = _userData

    fun onLoginPressed() {
        navigateToNewFragment(ProfileFragmentDirections.actionProfileFragmentToLoginBottomSheetFragment())
    }

    fun userLogin(loginBody: PostLoginBody) {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val result = userLoginUseCase.invoke(loginBody)) {
                is ResultDomain.Success -> {
                    val data = result.data

                    sharedPreferences.saveLoginToken(data.accessToken)
                    sharedPreferences.saveLoginRefreshToken(data.refreshToken)
                    sharedPreferences.saveUsername(loginBody.username)
                    sharedPreferences.savePassword(loginBody.password)

                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("დაფიქსირდა გარკვეული შეცდომა, გთხოვთ სცადოთ თავიდან")
                    }
                }
            }
        }
    }

    fun userLogout() {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val result = userLogOutUseCase.invoke()) {
                is ResultDomain.Success -> {
                    sharedPreferences.saveLoginToken("")
                    sharedPreferences.saveLoginRefreshToken("")
                    sharedPreferences.saveUserId(-1)

                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ანგარიშიდან გასვლა - ${result.exception}")
                    }
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            when (val result = userDataUseCase.invoke()) {
                is ResultDomain.Success -> {
                    val data = result.data.data

                    sharedPreferences.saveUserId(data.id)

                    _userData.value = data
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("მომხმარებლის მონაცემები - ${result.exception}")
                    }
                }
            }
        }
    }

    fun refreshProfileOnLogin() {
        navigateToNewFragment(ProfileFragmentDirections.actionProfileFragmentSelf())
    }

    fun deleteContinueWatchingFromRoomFull() {
        viewModelScope.launch(Dispatchers.IO) {
            dbDeleteAllContinueWatchingUseCase.invoke()
            newToastMessage("წარმატებით წაიშალა ბაზა")
        }
    }

//    fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>> {
//        return environment.databaseRepository.getContinueWatchingFromRoom()
//    }

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