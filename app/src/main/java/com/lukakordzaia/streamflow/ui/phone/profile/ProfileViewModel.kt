package com.lukakordzaia.streamflow.ui.phone.profile

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.TraktvDeviceCode
import com.lukakordzaia.streamflow.datamodels.TraktvGetToken
import com.lukakordzaia.streamflow.datamodels.TraktvRequestToken
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.TraktvRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.phone.MainActivity
import com.lukakordzaia.streamflow.ui.tv.TvActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: TraktvRepository) : BaseViewModel() {
    private val _traktvDeviceCode = MutableLiveData<TraktvDeviceCode>()
    val traktvDeviceCode: LiveData<TraktvDeviceCode> = _traktvDeviceCode

    private var counter: Long = 0
    private val validationCounter = MutableLiveData<Long>(0)

    private val _traktvUserToken = MutableLiveData<TraktvGetToken>()
    val traktvUserToken: LiveData<TraktvGetToken> = _traktvUserToken

    fun getDeviceCode() {
        viewModelScope.launch {
            when (val deviceCode = repository.getDeviceCode()) {
                is Result.Success -> {
                    val data = deviceCode.data

                    _traktvDeviceCode.value = data
                }
            }
        }
    }

    fun getUserToken(tokenRequest: TraktvRequestToken) {
        viewModelScope.launch {
            when (val token = repository.getUserToken(tokenRequest)) {
                is Result.Success -> {
                    val data = token.data

                    _traktvUserToken.value = data
                    Log.d("traktvtoken", data.toString())
                }
                is Result.Error -> {
                    when (token.exception) {
                        AppConstants.TRAKTV_PENDING_AUTH -> {
                            if (validationCounter.value!! < "${traktvDeviceCode.value!!.expiresIn}00".toLong()) {
                                Log.d("traktvtoken", token.exception)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    getUserToken(tokenRequest)
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

    fun deleteWatchedHistory(context: Context) {
        viewModelScope.launch {
            ImoviesDatabase.getDatabase(context)?.getDao()?.deleteDBContent()
        }
    }

    fun onDeletePressedPhone(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun onDeletePressedTv(context: Context) {
        val intent = Intent(context, TvActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}