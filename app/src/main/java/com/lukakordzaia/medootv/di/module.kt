package com.lukakordzaia.medootv.di

import com.lukakordzaia.medootv.helpers.SpinnerClass
import com.lukakordzaia.medootv.network.NetworkConnectionInterceptor
import com.lukakordzaia.medootv.network.RetrofitBuilder
import com.lukakordzaia.medootv.repository.*
import com.lukakordzaia.medootv.ui.phone.categories.CategoriesViewModel
import com.lukakordzaia.medootv.ui.phone.categories.singlegenre.SingleCategoryViewModel
import com.lukakordzaia.medootv.ui.phone.home.HomeViewModel
import com.lukakordzaia.medootv.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.medootv.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.medootv.ui.phone.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.medootv.ui.tv.categories.TvCategoriesViewModel
import com.lukakordzaia.medootv.ui.tv.details.TvDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SingleTitleViewModel(get()) }
    viewModel { VideoPlayerViewModel(get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { SingleCategoryViewModel(get()) }
    viewModel { SearchTitlesViewModel(get()) }
    viewModel { TvDetailsViewModel(get()) }
    viewModel { TvCategoriesViewModel(get()) }
}

val repositoryModule = module {
    single { HomeRepository(get()) }
    single { SingleTitleRepository(get()) }
    single { CategoriesRepository(get()) }
    single { SearchTitleRepository(get()) }
    single { TvDetailsRepository(get()) }
}

val generalModule = module {
    single { RetrofitBuilder(get()) }
    single { NetworkConnectionInterceptor(get()) }
    single { SpinnerClass(get()) }
}