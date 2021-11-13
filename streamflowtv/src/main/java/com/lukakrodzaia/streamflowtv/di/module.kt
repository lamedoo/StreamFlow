package com.lukakrodzaia.streamflowtv.di

import com.lukakrodzaia.streamflowtv.animations.TvSidebarAnimations
import com.lukakrodzaia.streamflowtv.ui.genres.SingleGenreViewModel
import com.lukakrodzaia.streamflowtv.ui.login.TvProfileViewModel
import com.lukakrodzaia.streamflowtv.ui.main.TvMainViewModel
import com.lukakrodzaia.streamflowtv.ui.search.TvSearchTitlesViewModel
import com.lukakrodzaia.streamflowtv.ui.tvcatalogue.TvCatalogueViewModel
import com.lukakrodzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakrodzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles.TvTitleFilesViewModel
import com.lukakrodzaia.streamflowtv.ui.tvwatchlist.TvWatchlistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tvViewModelModule = module {
    viewModel { TvMainViewModel() }
    viewModel { TvTitleDetailsViewModel() }
    viewModel { TvCatalogueViewModel() }
    viewModel { TvTitleFilesViewModel() }
    viewModel { SingleGenreViewModel() }
    viewModel { TvProfileViewModel() }
    viewModel { TvSearchTitlesViewModel() }
    viewModel { TvWatchlistViewModel() }
}

val tvGeneralModule = module {
    single { TvSidebarAnimations() }
}