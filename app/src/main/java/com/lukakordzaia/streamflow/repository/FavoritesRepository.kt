package com.lukakordzaia.streamflow.repository

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.datamodels.AddFavoritesModel
import com.lukakordzaia.streamflow.interfaces.FavoritesCallBack
import kotlinx.coroutines.tasks.await

class FavoritesRepository {
    suspend fun addTitleToFavorites(currentUserUid: String, addFavoritesModel: AddFavoritesModel): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(currentUserUid)
                .collection("favMovies")
                .document(addFavoritesModel.id.toString())
                .set(
                    mapOf(
                        "name" to addFavoritesModel.name,
                        "isTvShow" to addFavoritesModel.isTvShow,
                        "id" to addFavoritesModel.id,
                        "imdbId" to addFavoritesModel.imdbId
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