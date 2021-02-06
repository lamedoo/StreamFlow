package com.lukakordzaia.imoviesapp.ui.tv.details.titlefiles

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.database.DbDetails
import com.lukakordzaia.imoviesapp.helpers.SpinnerClass
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setMargin
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.tv_title_files_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TvTitleFilesFragment : Fragment(R.layout.tv_title_files_fragment_new) {
    private val tvDetailsViewModel by viewModel<TvDetailsViewModel>()
    private lateinit var tvTitleFilesEpisodesAdapter: TvTitleFilesEpisodesAdapter
    private var trailerUrl: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerClass = SpinnerClass(requireContext())
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        tvDetailsViewModel.getSingleTitleData(titleId)
        tvDetailsViewModel.getSingleTitleFiles(titleId)

        tvDetailsViewModel.checkTitleInDb(requireContext(), titleId).observe(viewLifecycleOwner, {
            tvDetailsViewModel.titleIsInDb(it)
        })

        // RIGHT SIDE
        tvDetailsViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                tv_no_files.setGone()
                tv_right_container_files.setVisible()
            }
        })

        tvDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(tv_files_spinner_language1, languages) { language ->
                tvDetailsViewModel.getTitleLanguageFiles(language)
            }
        })

        tvDetailsViewModel.titleIsInDb.observe(viewLifecycleOwner, { exists ->
            if (exists) {
                tvDetailsViewModel.getSingleWatchedTitleDetails(requireContext(), titleId).observe(viewLifecycleOwner, {
                    tv_continue_play_button.setOnClickListener { _ ->
                        continueTitlePlay(it)
                    }

                    if (isTvShow) {
                        tv_continue_play_button.text = String.format("სეზონი: ${it.season} ეპიზოდი: ${it.episode} / %02d:%02d - ${it.language}",
                                TimeUnit.MILLISECONDS.toMinutes(it.watchedTime),
                                TimeUnit.MILLISECONDS.toSeconds(it.watchedTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedTime))
                        )
                    } else {
                        tv_continue_play_button.text = String.format("განაგრძეთ ყურება %02d:%02d - ${it.language}",
                                TimeUnit.MILLISECONDS.toMinutes(it.watchedTime),
                                TimeUnit.MILLISECONDS.toSeconds(it.watchedTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedTime))
                        )
                    }
                })
                tv_continue_play_button.setVisible()
                tv_continue_play_button.onFocusChangeListener = OnPlayButtonFocus(tv_continue_play_button)
                tv_continue_play_button.requestFocus()
                tv_play_button.text = "თავიდან ყურება"
                tv_play_button.setMargin(0, 10, 0, 0)

                val seasonTitleConstraint = tv_files_season_title1.layoutParams as ConstraintLayout.LayoutParams
                val seasonSpinnerConstraint = tv_files_spinner_season1.layoutParams as ConstraintLayout.LayoutParams
                seasonTitleConstraint.topToBottom = tv_language_title.id
                seasonSpinnerConstraint.topToBottom = tv_language_title.id
                tv_files_season_title1.requestLayout()
                tv_files_season_title1.requestLayout()
            } else {
                tv_play_button.requestFocus()
                tv_continue_play_button.setGone()
            }
        })

        if (!isTvShow) {
            tv_files_season_title1.setGone()
            tv_files_spinner_season1.setGone()
            rv_tv_files_episodes.setGone()

            tv_play_button.setOnClickListener {
                playTitleFromStart(titleId, isTvShow)
            }
        } else {
            tv_play_button.setGone()
            tvDetailsViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                val seasonCount = Array(numOfSeasons!!) { i -> (i * 1) + 1 }.toList()
                spinnerClass.createSpinner(tv_files_spinner_season1, seasonCount) {
                    this.tvDetailsViewModel.getSeasonFiles(titleId, it.toInt())
                }
            })
            rv_tv_files_episodes.requestFocus()

            tvDetailsViewModel.episodeNames.observe(viewLifecycleOwner, {
                Log.d("episodes", it.toString())
                tvTitleFilesEpisodesAdapter.setEpisodeList(it)
            })
        }

        tv_play_button.onFocusChangeListener = OnPlayButtonFocus(tv_play_button)

        tvTitleFilesEpisodesAdapter = TvTitleFilesEpisodesAdapter(requireContext()) {
            tvDetailsViewModel.getEpisodeFile(it)
        }
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

    private fun continueTitlePlay(item: DbDetails) {
        trailerUrl = null
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("titleId", item.titleId)
        intent.putExtra("isTvShow", item.isTvShow)
        intent.putExtra("chosenLanguage", item.language)
        intent.putExtra("chosenSeason", item.season)
        intent.putExtra("chosenEpisode", item.episode)
        intent.putExtra("watchedTime", item.watchedTime)
        intent.putExtra("trailerUrl", trailerUrl)
        activity?.startActivity(intent)
    }

    inner class OnPlayButtonFocus(private val button: Button) : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (hasFocus) {
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#009c7c"))
                button.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                button.setTextColor(Color.parseColor("#009c7c"))
            }
        }

    }
}