package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.FavoritesCallBack
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import kotlinx.coroutines.tasks.await

class FavoritesRepository(retrofitBuilder: RetrofitBuilder): ImoviesCall() {
    private val service = retrofitBuilder.buildImoviesService()

    suspend fun getSearchFavoriteTitles(keywords: String, page: Int, year: String): Result<TitleList> {
        return imoviesCall { service.getSearchFavoriteTitles(keywords, page, year) }
    }

    suspend fun getSingleTitleData(titleId: Int): Result<SingleTitleData> {
        return imoviesCall { service.getSingleTitle(titleId) }
    }

    fun getFavTitlesFromFirestore(currentUserUid: String, favoritesCallBack: FavoritesCallBack) {
        val docRef = Firebase.firestore.collection("users").document(currentUserUid).collection("favMovies")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null ) {
                val movies: MutableList<Int> = ArrayList()
                val tvShows: MutableList<Int> = ArrayList()
                for (title in snapshot) {
                    if (title.data["isTvShow"] == true) {
                        tvShows.add(title.data["id"].toString().toInt())
                    } else {
                        movies.add(title.data["id"].toString().toInt())
                    }

                }
                favoritesCallBack.moviesList(movies)
                favoritesCallBack.tvShowsList(tvShows)
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    suspend fun removeFavTitleFromFirestore(currentUserUid: String, titleId: Int): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(currentUserUid)
                .collection("favMovies")
                .document(titleId.toString())
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}