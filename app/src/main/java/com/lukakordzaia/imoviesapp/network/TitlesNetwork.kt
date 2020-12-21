package com.lukakordzaia.imoviesapp.network


import com.lukakordzaia.imoviesapp.network.datamodels.TitleData
import com.lukakordzaia.imoviesapp.network.datamodels.TitleFiles
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TitlesNetwork {

//    Top Movies - movies/top?type=movie&period=day&page=1&per_page=10

    @Headers("User-Agent: imovies")
    @GET ("movies?page=1&per_page=15&filters%5Btype%5D=movie&filters%5Bwith_files%5D=yes&sort=-upload_date")
    suspend fun getMovies() : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("movies?page=3&per_page=15&filters%5Btype%5D=series&filters%5Bwith_files%5D=yes&sort=-upload_date")
    suspend fun getTvShows() : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("search-advanced?filters%5Btype%5D=movie&page=1&per_page=15")
    suspend fun getSearchTitles(@Query("keywords") keywords: String) : Response<TitleList>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/")
    suspend fun getSingleTitle(@Path("id") id: Int) : Response<TitleData>

    @Headers("User-Agent: imovies")
    @GET ("movies/{id}/season-files/{season_number}")
    suspend fun getSingleFiles(@Path("id") id: Int, @Path("season_number") season_number: Int) : Response<TitleFiles>
}