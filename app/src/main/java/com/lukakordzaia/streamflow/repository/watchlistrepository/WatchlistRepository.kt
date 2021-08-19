package com.lukakordzaia.streamflow.repository.watchlistrepository

import com.google.firebase.firestore.DocumentSnapshot
import com.lukakordzaia.streamflow.datamodels.AddFavoritesModel
import com.lukakordzaia.streamflow.interfaces.FavoritesCallBack
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserWatchlistResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.UserWatchListStatusResponse

interface WatchlistRepository {
    suspend fun addTitleToFavorites(currentUserUid: String, addFavoritesModel: AddFavoritesModel): Boolean
    suspend fun removeTitleFromFavorites(currentUserUid: String, titleId: Int): Boolean
    suspend fun checkTitleInFavorites(currentUserUid: String, titleId: Int): DocumentSnapshot?
    fun getTitlesFromFavorites(currentUserUid: String, favoritesCallBack: FavoritesCallBack)

    suspend fun getUserWatchlist() : Result<GetUserWatchlistResponse>
    suspend fun addWatchlistTitle(id: Int) : Result<UserWatchListStatusResponse>
    suspend fun deleteWatchlistTitle(id: Int) : Result<UserWatchListStatusResponse>
}