package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukakordzaia.core.baseclasses.BaseFragmentVM
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.domainmodels.VideoPlayerData
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.utils.applyBundle
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.databinding.FragmentTvEpisodesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvEpisodesFragment : BaseFragmentVM<FragmentTvEpisodesBinding, TvEpisodesViewModel>() {
    var titleId: Int = 0

    private var firstSelection = true

    override val viewModel by viewModel<TvEpisodesViewModel>()
    override val reload: () -> Unit = {
        viewModel.getSingleTitleData(titleId)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvEpisodesBinding
        get() = FragmentTvEpisodesBinding::inflate

    override fun onStart() {
        super.onStart()
        viewModel.getContinueWatching(titleId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleId = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as? Int
        val videoPlayerData = activity?.intent?.getParcelableExtra(AppConstants.VIDEO_PLAYER_DATA) as? VideoPlayerData

        this.titleId = titleId ?: videoPlayerData!!.titleId

        viewModel.getSingleTitleData(this.titleId)

        fragmentObservers()
    }

    private fun fragmentObservers() {
        viewModel.getSingleTitleResponse.observe(viewLifecycleOwner, {
            setTitleInfo(it)
        })

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                setSeasonsFragment(it.season, it.episode)
            } else {
                setSeasonsFragment()
            }
        })
    }

    private fun setSeasonsFragment(season: Int? = null, episode: Int? = null) {
        childFragmentManager.beginTransaction()
            .replace(R.id.season_fragment, TvSeasonBrowse().applyBundle {
                if (season != null) {
                    putInt("season", season)
                }
                if (episode != null) {
                    putInt("episode", episode)
                }
            })
            .commit()
    }

    fun setEpisodesFragment(season: Int? = null, episode: Int? = null) {
        childFragmentManager.beginTransaction()
            .replace(R.id.episode_fragment, TvEpisodesBrowse().applyBundle {
                if (season != null) {
                    putInt("season", season)
                }
                if (episode != null) {
                    putInt("episode", if (firstSelection) episode else 1)
                }
            })
            .commit()
    }

    fun setFragmentFocus(fragment: String) {
        when (fragment) {
            "season" -> {
                binding.root.findViewById<View>(R.id.season_fragment).requestFocus()
            }
            "episode" -> {
                binding.root.findViewById<View>(R.id.episode_fragment).requestFocus()
            }
        }
    }

    private fun setTitleInfo(info: SingleTitleModel) {
        binding.titleName.text = info.nameEng
        binding.year.text = info.releaseYear
        binding.imdbScore.text = getString(R.string.imdb_score, info.imdbScore)
        binding.country.text = info.country
        binding.duration.text = if (info.isTvShow) getString(R.string.season_number, info.seasonNum.toString()) else info.duration
    }

    fun isEpisodeFirstSelection(): Boolean = firstSelection
    fun setEpisodeFirstSelection(isFirst: Boolean) {
        firstSelection = isFirst
    }
}