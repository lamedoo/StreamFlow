package com.lukakordzaia.imoviesapp.di

import com.lukakordzaia.imoviesapp.network.RetrofitBuilder
import com.lukakordzaia.imoviesapp.repository.*
import com.lukakordzaia.imoviesapp.ui.phone.genres.GenresViewModel
import com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre.SingleGenreViewModel
import com.lukakordzaia.imoviesapp.ui.phone.home.HomeViewModel
import com.lukakordzaia.imoviesapp.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.ui.phone.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SingleTitleViewModel(get()) }
    viewModel { VideoPlayerViewModel(get()) }
    viewModel { GenresViewModel(get()) }
    viewModel { SingleGenreViewModel(get()) }
    viewModel { SearchTitlesViewModel(get()) }
    viewModel { TvDetailsViewModel(get()) }
}

val repositoryModule = module {
    single { HomeRepository(get()) }
    single { SingleTitleRepository(get()) }
    single { GenresRepository(get()) }
    single { SearchTitleRepository(get()) }
    single { TvDetailsRepository(get()) }
}

val generalModule = module {
    single { RetrofitBuilder() }
}