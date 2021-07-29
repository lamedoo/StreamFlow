package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.datamodels.AddTitleToFirestore
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.interfaces.FavoritesCallBack
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.imovies.ImoviesCall
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import kotlinx.coroutines.tasks.await

class FavoritesRepository: ImoviesCall() {
    suspend fun addTitleToFavorites(currentUserUid: String, addTitleToFirestore: AddTitleToFirestore): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(currentUserUid)
                .collection("favMovies")
                .document(addTitleToFirestore.id.toString())
                .set(
                    mapOf(
                        "name" to addTitleToFirestore.name,
                        "isTvShow" to addTitleToFirestore.isTvShow,
                        "id" to addTitleToFirestore.id,
                        "imdbId" to addTitleToFirestore.imdbId
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeTitleFromFavorites(currentUserUid: String, titleId: Int): Boolean {
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

    suspend fun checkTitleInFavorites(currentUserUid: String, titleId: Int): DocumentSnapshot? {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(currentUserUid)
                .collection("favMovies")
                .document(titleId.toString())
                .get()
                .await()

            data
        } catch (e: Exception) {
            null
        }
    }

    fun getTitlesFromFavorites(currentUserUid: String, favoritesCallBack: FavoritesCallBack) {
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
}