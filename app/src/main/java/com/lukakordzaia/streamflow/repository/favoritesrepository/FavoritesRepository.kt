package com.lukakordzaia.streamflow.repository.favoritesrepository

import com.google.firebase.firestore.DocumentSnapshot
import com.lukakordzaia.streamflow.datamodels.AddFavoritesModel
import com.lukakordzaia.streamflow.interfaces.FavoritesCallBack

interface FavoritesRepository {
    suspend fun addTitleToFavorites(currentUserUid: String, addFavoritesModel: AddFavoritesModel): Boolean
    suspend fun removeTitleFromFavorites(currentUserUid: String, titleId: Int): Boolean
    suspend fun checkTitleInFavorites(currentUserUid: String, titleId: Int): DocumentSnapshot?
    fun getTitlesFromFavorites(currentUserUid: String, favoritesCallBack: FavoritesCallBack)
}