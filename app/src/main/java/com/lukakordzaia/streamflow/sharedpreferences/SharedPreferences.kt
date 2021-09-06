package com.lukakordzaia.streamflow.sharedpreferences

import android.content.Context

class SharedPreferences(context: Context) {
    private val loginTokenDefValue = ""
    private val loginRefreshTokenDefValue = ""
    private val usernameDefValue = ""
    private val passwordDefValue = ""
    private val traktTokenDefValue = ""
    private val traktRefreshTokenDefValue = ""
    private val tvVideoPlayerOnDefValue = false
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

    fun saveTvVideoPlayerOn(playerIsOn: Boolean) {
        authEditor.putBoolean(TV_VIDEO_PLAYER_ON, playerIsOn)
        authEditor.apply()
    }

    fun getTvVideoPlayerOn(): Boolean {
        return authPrefs.getBoolean(TV_VIDEO_PLAYER_ON, tvVideoPlayerOnDefValue)
    }

    fun saveFromWatchlist(position: Int) {
        authEditor.putInt(IS_FROM_WATCHLIST, position)
        authEditor.apply()
    }

    fun getFromWatchlist(): Int {
        return authPrefs.getInt(IS_FROM_WATCHLIST, fromWatchlistDefValue)
    }

    fun saveTraktToken(accessToken: String) {
        authEditor.putString(TRAKT_TOKEN, accessToken)
        authEditor.apply()
    }

    fun getTraktToken(): String? {
        return authPrefs.getString(TRAKT_TOKEN, traktTokenDefValue)
    }

    fun saveTraktRefreshToken(refreshToken: String) {
        authEditor.putString(TRAKT_REFRESH_TOKEN, refreshToken)
        authEditor.apply()
    }

    fun getTraktRefreshToken(): String? {
        return authPrefs.getString(TRAKT_REFRESH_TOKEN, traktRefreshTokenDefValue)
    }

    companion object {
        const val PREF_NAME = "Auth"
        const val LOGIN_TOKEN = "login token"
        const val LOGIN_REFRESH_TOKEN = "login refresh token"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val TRAKT_TOKEN = "trakt token"
        const val TRAKT_REFRESH_TOKEN = "trakt refresh token"
        const val TV_VIDEO_PLAYER_ON = "video player is on"
        const val IS_FROM_WATCHLIST = "is from watchlist"
    }
}