package com.lukakordzaia.streamflow.sharedpreferences

import android.content.Context

class AuthSharedPreferences(context: Context) {
    private val PREF_NAME = "Auth"
    private val accessTokenDefValue = ""
    private val refreshTokenDefValue = ""
    private val authPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val authEditor = authPrefs.edit()

    fun saveAccessToken(accessToken: String) {
        authEditor.putString("access_token", accessToken)
        authEditor.apply()
    }

    fun getAccessToken(): String? {
        return authPrefs.getString("access_token", accessTokenDefValue)
    }

    fun saveRefreshToken(refreshToken: String) {
        authEditor.putString("refresh_token", refreshToken)
        authEditor.apply()
    }

    fun getRefreshToken(): String? {
        return authPrefs.getString("refresh_token", refreshTokenDefValue)
    }
}