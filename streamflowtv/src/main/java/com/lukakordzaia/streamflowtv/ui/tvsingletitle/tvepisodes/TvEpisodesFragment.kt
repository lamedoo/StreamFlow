package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.lukakordzaia.core.baseclasses.BaseFragmentVM
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.databinding.FragmentTvEpisodesBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvEpisodesFragment : BaseFragmentVM<FragmentTvEpisodesBinding, TvEpisodesViewModel>() {
    var titleId: Int? = 0

    override val viewModel by sharedViewModel<TvEpisodesViewModel>()
    override val reload: () -> Unit = {
        viewModel.getSingleTitleData(titleId!!)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvEpisodesBinding
        get() = FragmentTvEpisodesBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleIdFromDetails = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as? Int

        titleId = titleIdFromDetails

        viewModel.getSingleTitleData(titleId!!)

        fragmentObservers()
    }

    private fun fragmentObservers() {
        viewModel.getSingleTitleResponse.observe(viewLifecycleOwner, {
            setTitleInfo(it)
        })

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
//            setFragments()
            if (it != null) {
                binding.root.findViewById<View>(R.id.episode_fragment).requestFocus()
            } else {
                binding.root.findViewById<View>(R.id.season_fragment).requestFocus()
            }
        })
    }

    private fun setFragments() {
        childFragmentManager.beginTransaction()
            .replace(R.id.season_fragment, TvSeasonBrowse())
            .replace(R.id.episode_fragment, TvEpisodesBrowse())
            .commit()
    }

    private fun setTitleInfo(info: SingleTitleModel) {
        binding.titleName.text = info.nameEng
        binding.year.text = info.releaseYear
        binding.imdbScore.text = getString(R.string.imdb_score, info.imdbScore)
        binding.country.text = info.country
        binding.duration.text = if (info.isTvShow) getString(R.string.season_number, info.seasonNum.toString()) else info.duration
    }
}