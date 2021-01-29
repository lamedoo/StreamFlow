package com.lukakordzaia.imoviesapp.ui.tv.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.helpers.SpinnerClass
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.createToast
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setMargin
import com.lukakordzaia.imoviesapp.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_details_fragment.*
import java.util.concurrent.TimeUnit

class TvDetailsFragment : Fragment(R.layout.tv_details_fragment) {
    private lateinit var chooseTitleDetailsViewModel: ChooseTitleDetailsViewModel
    private lateinit var singleTitleViewModel: SingleTitleViewModel
    private lateinit var tvDetailsEpisodesAdapter: TvDetailsEpisodesAdapter
    private var trailerUrl: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerClass = SpinnerClass(requireContext())
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        chooseTitleDetailsViewModel = ViewModelProvider(this).get(ChooseTitleDetailsViewModel::class.java)
        singleTitleViewModel = ViewModelProvider(this).get(SingleTitleViewModel::class.java)

        singleTitleViewModel.getSingleTitleData(titleId)
        chooseTitleDetailsViewModel.getSingleTitleFiles(titleId)

        // LEFT SIDE
        singleTitleViewModel.singleTitleData.observe(viewLifecycleOwner, {
            tv_files_trailer.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db_focused, null)
                } else {
                    v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db, null)
                }
            }

            tv_files_trailer.setOnClickListener { _ ->
                if (it.trailers != null) {
                    if (!it.trailers.data.isNullOrEmpty()) {
                        it.trailers.data.forEach { trailer ->
                            if (trailer.language == "ENG") {
                                trailerUrl = trailer.fileUrl
                                val intent = Intent(context, TvVideoPlayerActivity::class.java)
                                intent.putExtra("titleId", titleId)
                                intent.putExtra("isTvShow", isTvShow)
                                intent.putExtra("chosenLanguage", "ENG")
                                intent.putExtra("chosenSeason", 0)
                                intent.putExtra("chosenEpisode", 0)
                                intent.putExtra("watchedTime", 0L)
                                intent.putExtra("trailerUrl", trailerUrl)
                                activity?.startActivity(intent)
                            } else {
                                requireContext().createToast("other trailer")
                            }
                        }
                    } else {
                        requireContext().createToast("no trailer")
                    }
                } else {
                    requireContext().createToast("no trailer")
                }
            }

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
            singleTitleViewModel.titleIsInDb(it)
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


        // RIGHT SIDE
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

        singleTitleViewModel.titleIsInDb.observe(viewLifecycleOwner, { exists ->
            if (exists) {
                tv_files_title_delete.setVisible()

                singleTitleViewModel.getSingleWatchedTitleDetails(requireContext(), titleId).observe(viewLifecycleOwner, {
                    tv_continue_play_button.setOnClickListener { _ ->
                        continueTitlePlay(it)
                    }

                    if (isTvShow) {
                        tv_continue_play_button.text = "სეზონი: ${it.season} ეპიზოდი: ${it.episode} / ${String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(it.watchedTime),
                            TimeUnit.MILLISECONDS.toSeconds(it.watchedTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedTime))
                        )} - ${it.language}"
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
            chooseTitleDetailsViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                val seasonCount = Array(numOfSeasons!!) { i -> (i * 1) + 1 }.toList()
                spinnerClass.createSpinner(tv_files_spinner_season1, seasonCount) {
                    chooseTitleDetailsViewModel.getSeasonFiles(titleId, it.toInt())
                }
            })
            rv_tv_files_episodes.requestFocus()

            chooseTitleDetailsViewModel.episodeNames.observe(viewLifecycleOwner, {
                tvDetailsEpisodesAdapter.setEpisodeList(it)
            })
        }

        tv_play_button.onFocusChangeListener = OnPlayButtonFocus(tv_play_button)

        tvDetailsEpisodesAdapter = TvDetailsEpisodesAdapter(requireContext()) {
            chooseTitleDetailsViewModel.getEpisodeFile(it)
        }
        rv_tv_files_episodes.adapter = tvDetailsEpisodesAdapter

        chooseTitleDetailsViewModel.chosenEpisode.observe(viewLifecycleOwner, {
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
        intent.putExtra("chosenLanguage", chooseTitleDetailsViewModel.chosenLanguage.value)
        intent.putExtra("chosenSeason", chooseTitleDetailsViewModel.chosenSeason.value)
        intent.putExtra("chosenEpisode", chooseTitleDetailsViewModel.chosenEpisode.value)
        intent.putExtra("watchedTime", 0L)
        intent.putExtra("trailerUrl", trailerUrl)
        activity?.startActivity(intent)
    }

    private fun continueTitlePlay(item: WatchedDetails) {
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