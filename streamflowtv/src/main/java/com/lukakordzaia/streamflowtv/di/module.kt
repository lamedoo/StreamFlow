package com.lukakordzaia.streamflowtv.di

import com.lukakordzaia.streamflowtv.animations.TvSidebarAnimations
import com.lukakordzaia.streamflowtv.ui.genres.SingleGenreViewModel
import com.lukakordzaia.streamflowtv.ui.login.TvProfileViewModel
import com.lukakordzaia.streamflowtv.ui.main.TvMainViewModel
import com.lukakordzaia.streamflowtv.ui.search.TvSearchTitlesViewModel
import com.lukakordzaia.streamflowtv.ui.tvcatalogue.TvCatalogueViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes.TvEpisodesViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.TvRelatedViewModel
import com.lukakordzaia.streamflowtv.ui.tvwatchlist.TvWatchlistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tvViewModelModule = module {
    viewModel { TvMainViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { TvTitleDetailsViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { TvCatalogueViewModel(get(), get(), get()) }
    viewModel { TvRelatedViewModel(get(), get()) }
    viewModel { SingleGenreViewModel(get()) }
    viewModel { TvProfileViewModel(get(), get(), get(), get()) }
    viewModel { TvSearchTitlesViewModel(get()) }
    viewModel { TvWatchlistViewModel(get()) }
    viewModel { TvEpisodesViewModel(get(), get(), get()) }
}

val tvGeneralModule = module {
    single { TvSidebarAnimations() }
}