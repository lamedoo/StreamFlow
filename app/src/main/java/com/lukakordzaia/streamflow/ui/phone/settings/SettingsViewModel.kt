package com.lukakordzaia.streamflow.ui.phone.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.TraktvDeviceCode
import com.lukakordzaia.streamflow.di.repositoryModule
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.TraktvRepository
import com.lukakordzaia.streamflow.ui.phone.MainActivity
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.tv.TvActivity
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: TraktvRepository) : BaseViewModel() {

    private val _traktvDeviceCode = MutableLiveData<TraktvDeviceCode>()
    val traktvDeviceCode: LiveData<TraktvDeviceCode> = _traktvDeviceCode

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