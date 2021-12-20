package com.lukakordzaia.core.sharedpreferences

import android.content.Context

class SharedPreferences(context: Context) {
    private val loginTokenDefValue = ""
    private val loginRefreshTokenDefValue = ""
    private val usernameDefValue = ""
    private val passwordDefValue = ""
    private val userIdDefValue = -1
    private val refreshContinueWatchingDefValue = false
    private val refreshTitleDetailsDefValue = false
    private val fromWatchlistDefValue = -1
    private val authPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val authEditor = authPrefs.edit()

    fun saveLoginToken(token: String) {
        authEditor.putString(LOGIN_TOKEN, token)
        authEditor.apply()
    }

    fun getLoginToken(): String? {
        return authPrefs.getString(LOGIN_TOKEN, loginTokenDefValue)
    }

    fun saveLoginRefreshToken(token: String) {
        authEditor.putString(LOGIN_REFRESH_TOKEN, token)
        authEditor.apply()
    }

    fun getLoginRefreshToken(): String? {
        return authPrefs.getString(LOGIN_REFRESH_TOKEN, loginRefreshTokenDefValue)
    }

    fun saveUsername(username: String) {
        authEditor.putString(USERNAME, username)
        authEditor.apply()
    }

    fun getUsername(): String? {
        return authPrefs.getString(USERNAME, usernameDefValue)
    }

    fun savePassword(password: String) {
        authEditor.putString(PASSWORD, password)
        authEditor.apply()
    }

    fun getPassword(): String? {
        return authPrefs.getString(PASSWORD, passwordDefValue)
    }

    fun saveRefreshContinueWatching(refresh: Boolean) {
        authEditor.putBoolean(REFRESH_CONTINUE_WATCHING, refresh)
        authEditor.apply()
    }

    fun getRefreshContinueWatching(): Boolean {
        return authPrefs.getBoolean(REFRESH_CONTINUE_WATCHING, refreshContinueWatchingDefValue)
    }

    fun saveRefreshTitleDetails(refresh: Boolean) {
        authEditor.putBoolean(REFRESH_TITLE_DETAILS, refresh)
        authEditor.apply()
    }

    fun getRefreshTitleDetails(): Boolean {
        return authPrefs.getBoolean(REFRESH_TITLE_DETAILS, refreshTitleDetailsDefValue)
    }

    fun saveFromWatchlist(position: Int) {
        authEditor.putInt(IS_FROM_WATCHLIST, position)
        authEditor.apply()
    }

    fun getFromWatchlist(): Int {
        return authPrefs.getInt(IS_FROM_WATCHLIST, fromWatchlistDefValue)
    }

    fun getUserId(): Int {
        return authPrefs.getInt(USER_ID, userIdDefValue)
    }

    fun saveUserId(userId: Int) {
        authEditor.putInt(USER_ID, userId)
        authEditor.apply()
    }

    companion object {
        const val PREF_NAME = "Auth"
        const val LOGIN_TOKEN = "login token"
        const val LOGIN_REFRESH_TOKEN = "login refresh token"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val USER_ID = "user id"
        const val REFRESH_CONTINUE_WATCHING = "refresh continue watching"
        const val REFRESH_TITLE_DETAILS = "refresh title details"
        const val IS_FROM_WATCHLIST = "is from watchlist"
    }
}