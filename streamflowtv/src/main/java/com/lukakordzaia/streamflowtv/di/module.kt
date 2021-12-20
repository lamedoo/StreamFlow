package com.lukakordzaia.streamflowtv.di

import com.lukakordzaia.streamflowtv.animations.TvSidebarAnimations
import com.lukakordzaia.streamflowtv.ui.genres.SingleGenreViewModel
import com.lukakordzaia.streamflowtv.ui.login.TvProfileViewModel
import com.lukakordzaia.streamflowtv.ui.main.TvMainViewModel
import com.lukakordzaia.streamflowtv.ui.search.TvSearchTitlesViewModel
import com.lukakordzaia.streamflowtv.ui.tvcatalogue.TvCatalogueViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles.TvTitleFilesViewModel
import com.lukakordzaia.streamflowtv.ui.tvwatchlist.TvWatchlistViewModel
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