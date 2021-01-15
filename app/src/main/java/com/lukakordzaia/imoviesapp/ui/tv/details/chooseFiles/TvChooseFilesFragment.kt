package com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.SpinnerClass
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.fragment_choose_title_details.*
import kotlinx.android.synthetic.main.tv_choose_files_fragment.*

class TvChooseFilesFragment : Fragment(R.layout.tv_choose_files_fragment) {
    private lateinit var viewModel: ChooseTitleDetailsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
        val numOfSeasons = activity?.intent?.getSerializableExtra("numOfSeasons") as Int
        viewModel = ViewModelProvider(this).get(ChooseTitleDetailsViewModel::class.java)
        viewModel.getSingleTitleFiles(titleId)
        val spinnerClass = SpinnerClass(requireContext())

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                tv_file_not_yet.setGone()
                tv_files_container.setVisible()
            }
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(tv_files_spinner_language, languages) { language ->
                viewModel.getTitleLanguageFiles(language)
            }
        })

        if (!isTvShow) {
            tv_files_season_title.setGone()
            tv_files_spinner_season.setGone()
            tv_files_episode_title.setGone()
            tv_files_spinner_episode.setGone()
        } else {
            val seasonCount = Array(numOfSeasons) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(tv_files_spinner_season, seasonCount) {
                viewModel.getSeasonFiles(titleId, it.toInt())
            }
        }

        viewModel.availableEpisodes.observe(viewLifecycleOwner, { it ->
            val numOfEpisodes = Array(it) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(tv_files_spinner_episode, numOfEpisodes) { episode ->
                viewModel.getEpisodeFile(episode.toInt())
            }
        })

        tv_play.setOnClickListener {
            val intent = Intent(context, TvVideoPlayerActivity::class.java)
            intent.putExtra("titleId", titleId)
            intent.putExtra("isTvShow", isTvShow)
            intent.putExtra("chosenLanguage", viewModel.chosenLanguage.value)
            intent.putExtra("chosenSeason", viewModel.chosenSeason.value)
            intent.putExtra("chosenEpisode", viewModel.chosenEpisode.value)
            intent.putExtra("watchedTime", 0L)
            activity?.startActivity(intent)
        }
    }
}