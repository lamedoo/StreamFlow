package com.lukakordzaia.streamflow.ui.phone.profile

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.network.models.trakttv.response.GetUserTokenResponse
import com.lukakordzaia.streamflow.network.models.trakttv.request.AddNewListRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.request.GetUserTokenRequestBody
import com.lukakordzaia.streamflow.network.models.trakttv.response.GetDeviceCodeResponse
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.ProfileRepository
import com.lukakordzaia.streamflow.repository.TraktRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.tv.TvActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileRepository: ProfileRepository, private val repository: TraktRepository) : BaseViewModel() {
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
            when (val deviceCode = repository.getDeviceCode()) {
                is Result.Success -> {
                    val data = deviceCode.data

                    _traktDeviceCode.value = data
                }
            }
        }
    }

    fun getUserToken(tokenRequestRequestBody: GetUserTokenRequestBody) {
        viewModelScope.launch {
            when (val token = repository.getUserToken(tokenRequestRequestBody)) {
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
            when (val list = repository.createNewList(newList, accessToken)) {
                is Result.Success -> {
                    Log.d("traktvlist", "წარმატებულია")
                }
            }
        }
    }

    private fun getSfList(accessToken: String) {
        viewModelScope.launch {
            when (val sfList = repository.getSfList(accessToken)) {
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

    fun deleteContinueWatchingFromRoomFull(context: Context) {
        viewModelScope.launch {
            roomDb(context)!!.deleteContinueWatchingFullFromRoom()
        }
    }

    fun deleteContinueWatchingFromFirestoreFull() {
        viewModelScope.launch {
            val getTitles = profileRepository.getContinueWatchingFromFirestore(currentUser()!!.uid)
            if (getTitles!!.documents.isNotEmpty()) {
                for (title in getTitles.documents) {
                    deleteContinueWatchingTitleFromFirestore(title.data!!["id"].toString().toInt())
                }
            } else {
                newToastMessage("ბაზა უკვე ცარიელია")
            }
        }
    }

    private fun deleteContinueWatchingTitleFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val deleteTitles = profileRepository.deleteContinueWatchingFullFromFirestore(currentUser()!!.uid, titleId)

            if (deleteTitles) {
                newToastMessage("ბაზა წარმატებით წაშლილია")
            }
        }
    }

    fun onDeletePressedTv(context: Context) {
        val intent = Intent(context, TvActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun createUserFirestore() {
        viewModelScope.launch {
            repository.createUserFirestore(currentUser())
        }
    }

    fun getContinueWatchingFromRoom(context: Context): LiveData<List<ContinueWatchingRoom>> {
        return profileRepository.getContinueWatchingFromRoom(roomDb(context)!!)
    }

    fun addContinueWatchingToFirestore(context: Context, continueWatchingRoomList: List<ContinueWatchingRoom>) {
        viewModelScope.launch {
            continueWatchingRoomList.forEach {
                val addToFirestore = profileRepository.addContinueWatchingTitleToFirestore(currentUser()!!.uid, it)
                if (addToFirestore) {
                    newToastMessage("სინქრონიზაცია წარმატებით დასრულდა")
                    deleteContinueWatchingFromRoomFull(context)
                    refreshProfileOnLogin()
                } else {
                    newToastMessage("სამწუხაროდ, ვერ მოხერხდა სინქრონიზაცია")
                }
            }
        }
    }
}