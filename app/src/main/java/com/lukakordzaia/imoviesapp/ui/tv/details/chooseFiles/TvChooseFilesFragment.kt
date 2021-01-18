package com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.SpinnerClass
import com.lukakordzaia.imoviesapp.utils.createToast
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_choose_files_fragment_new.*

class TvChooseFilesFragment : Fragment(R.layout.tv_choose_files_fragment_new) {
    private lateinit var viewModel: ChooseTitleDetailsViewModel
    private lateinit var tvChooseFilesEpisodesAdapter: TvChooseFilesEpisodesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
        val numOfSeasons = activity?.intent?.getSerializableExtra("numOfSeasons") as Int
        val titlePoster = activity?.intent?.getSerializableExtra("titlePoster") as String
        val titleName = activity?.intent?.getSerializableExtra("titleName") as String

        viewModel = ViewModelProvider(this).get(ChooseTitleDetailsViewModel::class.java)
        viewModel.getSingleTitleFiles(titleId)
        val spinnerClass = SpinnerClass(requireContext())

        Picasso.get().load(titlePoster).into(tv_files_title_poster)
        tv_files_title_name.text = titleName

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                tv_file_not_yet1.setGone()
                tv_files_container1.setVisible()
            }
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(tv_files_spinner_language1, languages) { language ->
                viewModel.getTitleLanguageFiles(language)
            }
        })

        if (!isTvShow) {
            tv_files_season_title1.setGone()
            tv_files_spinner_season1.setGone()
            rv_tv_files_episodes.setGone()
        } else {
            tv_play_button1.setGone()
            val seasonCount = Array(numOfSeasons) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(tv_files_spinner_season1, seasonCount) {
                viewModel.getSeasonFiles(titleId, it.toInt())
            }
        }

        viewModel.availableEpisodes.observe(viewLifecycleOwner, { it ->
            val numOfEpisodes = Array(it) { i -> (i * 1) + 1 }.toList()
            tvChooseFilesEpisodesAdapter.setEpisodeList(numOfEpisodes)
        })

        tvChooseFilesEpisodesAdapter = TvChooseFilesEpisodesAdapter(requireContext()) {
            requireContext().createToast("$it")
            viewModel.getEpisodeFile(it)
        }
        rv_tv_files_episodes.adapter = tvChooseFilesEpisodesAdapter

        viewModel.chosenEpisode.observe(viewLifecycleOwner, {
            if (it != 0) {
                openVideoPlayer(titleId, isTvShow)
            }
        })

        tv_play_button1.setOnClickListener {
            openVideoPlayer(titleId, isTvShow)
        }
    }

    fun openVideoPlayer(titleId: Int, isTvShow: Boolean) {
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