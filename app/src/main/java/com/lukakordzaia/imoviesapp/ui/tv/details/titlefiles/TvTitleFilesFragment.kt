package com.lukakordzaia.imoviesapp.ui.tv.details.titlefiles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.helpers.SpinnerClass
import com.lukakordzaia.imoviesapp.network.LoadingState
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.tv_title_files_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvTitleFilesFragment : Fragment(R.layout.tv_title_files_fragment), TvDetailsActivity.MyKeyEventListener {
    private val tvDetailsViewModel by viewModel<TvDetailsViewModel>()
    private lateinit var tvTitleFilesEpisodesAdapter: TvTitleFilesEpisodesAdapter
    private val spinnerClass: SpinnerClass by inject()
    private var trailerUrl: String? = null
    private var hasFocus = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        tvDetailsViewModel.getSingleTitleData(titleId)
        tvDetailsViewModel.getSingleTitleFiles(titleId)

        tvDetailsViewModel.loader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> {
                    rv_tv_files_episodes.setGone()
                }
                LoadingState.LOADED -> {
                    rv_tv_files_episodes.setVisible()
                    rv_tv_files_episodes.requestFocus()
                }
            }
        })

        if (!isTvShow) {
            tv_files_language_title.setGone()
            tv_files_spinner_language.setGone()
            tv_files_season_title.setGone()
            tv_files_spinner_season.setGone()
            rv_tv_files_episodes.setGone()
        } else {
            tvDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
                val languages = it.reversed()
                spinnerClass.createSpinner(tv_files_spinner_language, languages) { language ->
                    tvDetailsViewModel.getTitleLanguageFiles(language)
                }
            })

            tvDetailsViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                val seasonCount = Array(numOfSeasons!!) { i -> (i * 1) + 1 }.toList()
                spinnerClass.createSpinner(tv_files_spinner_season, seasonCount) {
                    this.tvDetailsViewModel.getSeasonFiles(titleId, it.toInt())
                }
            })

            tvDetailsViewModel.episodeNames.observe(viewLifecycleOwner, {
                Log.d("episodes", it.toString())
                tvTitleFilesEpisodesAdapter.setEpisodeList(it)
            })
        }

        val layout = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        tvTitleFilesEpisodesAdapter = TvTitleFilesEpisodesAdapter(requireContext(), {
            tvDetailsViewModel.getEpisodeFile(it)
        },
                {
                    this.hasFocus = it
                })
        rv_tv_files_episodes.layoutManager = layout
        rv_tv_files_episodes.adapter = tvTitleFilesEpisodesAdapter

        tvDetailsViewModel.chosenEpisode.observe(viewLifecycleOwner, {
            if (it != 0) {
                playTitleFromStart(titleId, isTvShow)
            }
        })
    }

    private fun playTitleFromStart(titleId: Int, isTvShow: Boolean) {
        trailerUrl = null
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("titleId", titleId)
        intent.putExtra("isTvShow", isTvShow)
        intent.putExtra("chosenLanguage", this.tvDetailsViewModel.chosenLanguage.value)
        intent.putExtra("chosenSeason", this.tvDetailsViewModel.chosenSeason.value)
        intent.putExtra("chosenEpisode", this.tvDetailsViewModel.chosenEpisode.value)
        intent.putExtra("watchedTime", 0L)
        intent.putExtra("trailerUrl", trailerUrl)
        activity?.startActivity(intent)
    }

    override fun onKeyDown(): Boolean {
        return hasFocus
    }
}