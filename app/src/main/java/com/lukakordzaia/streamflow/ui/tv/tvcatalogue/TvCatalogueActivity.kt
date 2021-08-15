package com.lukakordzaia.streamflow.ui.tv.tvcatalogue

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvCatalogueBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvCatalogueActivity : BaseFragmentActivity<ActivityTvCatalogueBinding>(), TvCheckTitleSelected {
    private val tvTitleDetailsViewModel: TvTitleDetailsViewModel by viewModel()

    override fun getViewBinding() = ActivityTvCatalogueBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSidebarClickListeners(binding.tvSidebar)
        setCurrentButton(binding.tvSidebar.moviesButton)
        googleViews(binding.tvSidebar)

        binding.tvSidebar.moviesButton.setOnClickListener {
            binding.tvSidebar.root.setGone()
        }

        binding.tvSidebarCollapsed.collapsedMoviesIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
    }

    override fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?) {
        tvTitleDetailsViewModel.getSingleTitleData(titleId)

        tvTitleDetailsViewModel.getSingleTitleResponse.observe(this, {
            binding.titleInfo.name.text = it.nameEng

            binding.titleInfo.poster.setImage(it.cover, false)

            binding.titleInfo.year.text = "${it.releaseYear}   ·"
            binding.titleInfo.duration.text = if (it.isTvShow) {
                "${it.seasonNum} სეზონი   ·"
            } else {
                "${it.duration}   ·"
            }

            binding.titleInfo.imdbScore.text = "IMDB ${it.imdbScore}"
        })

        tvTitleDetailsViewModel.titleGenres.observe(this, {
            binding.titleInfo.genres.text = TextUtils.join(", ", it)
        })
    }
}