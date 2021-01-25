package com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.helpers.SpinnerClass
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.createToast
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_choose_files_fragment.*

class TvChooseFilesFragment : Fragment(R.layout.tv_choose_files_fragment) {
    private lateinit var chooseTitleDetailsViewModel: ChooseTitleDetailsViewModel
    private lateinit var singleTitleViewModel: SingleTitleViewModel
    private lateinit var tvChooseFilesEpisodesAdapter: TvChooseFilesEpisodesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerClass = SpinnerClass(requireContext())
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        chooseTitleDetailsViewModel = ViewModelProvider(this).get(ChooseTitleDetailsViewModel::class.java)
        singleTitleViewModel = ViewModelProvider(this).get(SingleTitleViewModel::class.java)

        singleTitleViewModel.getSingleTitleData(titleId)
        chooseTitleDetailsViewModel.getSingleTitleFiles(titleId)

        singleTitleViewModel.singleTitleData.observe(viewLifecycleOwner, {
            Picasso.get().load(it.posters.data?.x240).into(tv_files_title_poster)

            if (it.covers != null) {
                if (it.covers.data != null) {
                    if (!it.covers.data.x1050.isNullOrEmpty()) {
                        Picasso.get().load(it.covers.data.x1050).into(tv_files_title_poster)
                    } else {
                        Picasso.get().load(R.drawable.movie_image_placeholder).into(tv_files_title_poster)
                    }
                }
            }

            tv_files_title_year.text = it.year.toString()
            if (it.rating?.imdb?.score != null) {
                tv_files_title_imdb_score.text = it.rating.imdb.score.toString()
            }
            tv_files_title_name_eng.text = it.secondaryName
            tv_files_title_name_geo.text = it.primaryName
            if (it.isTvShow == true) {
                chooseTitleDetailsViewModel.getNumOfSeasonsForTv(it.seasons!!.data!!.size)
            } else {
                tv_files_title_duration.text = "${it.duration.toString()} წთ."
            }
            if (it.plot.data != null) {
                if (!it.plot.data.description.isNullOrEmpty()) {
                    tv_files_title_desc.text = it.plot.data.description
                } else {
                    tv_files_title_desc.text = "აღწერა არ მოიძებნა"
                }
            } else {
                tv_files_title_desc.text = "აღწერა არ მოიძებნა"
            }
        })

        singleTitleViewModel.checkTitleInDb(requireContext(), titleId).observe(viewLifecycleOwner, {
            if (it) {
                tv_files_title_delete.setOnClickListener {
                    singleTitleViewModel.deleteTitleFromDb(requireContext(), titleId)
                }
                tv_files_title_delete.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db_focused, null)
                    } else {
                        v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db, null)
                    }
                }
            } else {
                tv_files_title_delete.setGone()
            }
        })


        chooseTitleDetailsViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                tv_no_files.setGone()
                tv_right_container_files.setVisible()
            }
        })

        chooseTitleDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(tv_files_spinner_language1, languages) { language ->
                chooseTitleDetailsViewModel.getTitleLanguageFiles(language)
            }
        })

        if (!isTvShow) {
            tv_files_season_title1.setGone()
            tv_files_spinner_season1.setGone()
            rv_tv_files_episodes.setGone()
        } else {
            tv_play_button1.setGone()
            chooseTitleDetailsViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                val seasonCount = Array(numOfSeasons!!) { i -> (i * 1) + 1 }.toList()
                spinnerClass.createSpinner(tv_files_spinner_season1, seasonCount) {
                    chooseTitleDetailsViewModel.getSeasonFiles(titleId, it.toInt())
                }
            })
        }

        chooseTitleDetailsViewModel.availableEpisodes.observe(viewLifecycleOwner, { it ->
            val numOfEpisodes = Array(it) { i -> (i * 1) + 1 }.toList()
            tvChooseFilesEpisodesAdapter.setEpisodeList(numOfEpisodes)
        })

        tvChooseFilesEpisodesAdapter = TvChooseFilesEpisodesAdapter(requireContext()) {
            requireContext().createToast("$it")
            chooseTitleDetailsViewModel.getEpisodeFile(it)
        }
        rv_tv_files_episodes.adapter = tvChooseFilesEpisodesAdapter

        chooseTitleDetailsViewModel.chosenEpisode.observe(viewLifecycleOwner, {
            if (it != 0) {
                openVideoPlayer(titleId, isTvShow)
            }
        })

        tv_play_button1.requestFocus()
        tv_play_button1.setOnClickListener {
            openVideoPlayer(titleId, isTvShow)
        }
    }

    fun openVideoPlayer(titleId: Int, isTvShow: Boolean) {
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("titleId", titleId)
        intent.putExtra("isTvShow", isTvShow)
        intent.putExtra("chosenLanguage", chooseTitleDetailsViewModel.chosenLanguage.value)
        intent.putExtra("chosenSeason", chooseTitleDetailsViewModel.chosenSeason.value)
        intent.putExtra("chosenEpisode", chooseTitleDetailsViewModel.chosenEpisode.value)
        intent.putExtra("watchedTime", 0L)
        activity?.startActivity(intent)
    }
}