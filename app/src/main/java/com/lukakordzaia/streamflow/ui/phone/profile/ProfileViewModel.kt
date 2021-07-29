package com.lukakordzaia.streamflow.ui.phone.profile

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingListCallBack
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.trakttv.request.AddNewListRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.request.GetUserTokenRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.response.GetDeviceCodeResponse
import com.lukakordzaia.streamflow.network.models.trakttv.response.GetUserTokenResponse
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class ProfileViewModel : BaseViewModel() {
    private val _traktDeviceCode = MutableLiveData<GetDeviceCodeResponse>()
    val traktDeviceCodeResponse: LiveData<GetDeviceCodeResponse> = _traktDeviceCode

    private var counter: Long = 0
    private val validationCounter = MutableLiveData<Long>(0)

    private val _traktUserToken = MutableLiveData<GetUserTokenResponse>(null)
    val userUserTokenResponse: LiveData<GetUserTokenResponse> = _traktUserToken

    private val _traktSfListExists = MutableLiveData<Boolean>()
    val traktSfListExists: LiveData<Boolean> = _traktSfListExists

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

    fun deleteContinueWatchingFromFirestoreFull() {
        environment.databaseRepository.getContinueWatchingFromFirestore(currentUser()!!.uid, object :
            FirebaseContinueWatchingListCallBack {
            override fun continueWatchingList(titleList: MutableList<ContinueWatchingRoom>) {
                if (titleList.isNullOrEmpty()) {
                    titleList.forEach {
                        deleteContinueWatchingTitleFromFirestore(it.titleId)
                    }
                } else {
                    newToastMessage("ბაზა უკვე ცარიელია")
                }
            }
        })
    }

    private fun deleteContinueWatchingTitleFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val deleteTitles = environment.databaseRepository.deleteSingleContinueWatchingFromFirestore(currentUser()!!.uid, titleId)

            if (deleteTitles) {
                newToastMessage("ბაზა წარმატებით წაშლილია")
            }
        }
    }

    fun createUserFirestore() {
        viewModelScope.launch {
            environment.databaseRepository.createUserFirestore(currentUser())
        }
    }

    fun getContinueWatchingFromRoom(): LiveData<List<ContinueWatchingRoom>> {
        return environment.databaseRepository.getContinueWatchingFromRoom()
    }

    fun addContinueWatchingToFirestore(continueWatchingRoomList: List<ContinueWatchingRoom>) {
        viewModelScope.launch {
            continueWatchingRoomList.forEach {
                val addToFirestore = environment.databaseRepository.addContinueWatchingTitleToFirestore(currentUser()!!.uid, it)
                if (addToFirestore) {
                    newToastMessage("სინქრონიზაცია წარმატებით დასრულდა")
                    deleteContinueWatchingFromRoomFull()
                    refreshProfileOnLogin()
                } else {
                    newToastMessage("სამწუხაროდ, ვერ მოხერხდა სინქრონიზაცია")
                }
            }
        }
    }
}