package com.lukakordzaia.streamflow.ui.tv.details.titledetails

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.helpers.SpinnerClass
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.tv.details.titlefiles.TvTitleFilesFragment
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_details_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TvDetailsFragment : Fragment(R.layout.tv_details_fragment) {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()
    private val spinnerClass: SpinnerClass by inject()
    private var trailerUrl: String? = null
    private var hasFocus: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        tvDetailsViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    tvDetailsViewModel.getSingleTitleData(titleId)
                    tvDetailsViewModel.getSingleTitleFiles(titleId)
                }, 5000)
            }
        })

        tvDetailsViewModel.getSingleTitleData(titleId)

        // For Languages
        tvDetailsViewModel.getSingleTitleFiles(titleId)

        tvDetailsViewModel.dataLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> tv_details_progressBar.setVisible()
                LoadingState.LOADED -> {
                    tv_details_progressBar.setGone()
                    tv_details_top.setVisible()
                }
            }
        })

        tvDetailsViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                tv_details_no_files.setGone()
                tv_details_buttons_row.setVisible()
                tv_details_language_title.setVisible()
                tv_details_spinner_language.setVisible()
            }
        })

        tvDetailsViewModel.singleTitleData.observe(viewLifecycleOwner, {

            tv_files_trailer.setOnClickListener { _ ->
                if (!it.trailers.data.isNullOrEmpty()) {
                    it.trailers.data.forEach { trailer ->
                        when (tvDetailsViewModel.chosenLanguage.value) {
                            "ENG" -> {
                                if (trailer.language == tvDetailsViewModel.chosenLanguage.value) {
                                    trailerUrl = trailer.fileUrl
                                    playTitleTrailer(titleId, isTvShow, trailerUrl!!)
                                }
                            }
                            else -> {
                                if (trailer.language == "RUS") {
                                    trailerUrl = trailer.fileUrl
                                    playTitleTrailer(titleId, isTvShow, trailerUrl!!)
                                }
                            }
                        }
                    }
                } else {
                    requireContext().createToast("no trailer")
                }
            }


            Picasso.get().load(it.covers?.data?.x1050).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(tv_files_title_poster)


            tv_files_title_year.text = it.year.toString()
            if (it.rating.imdb?.score != null) {
                tv_files_title_imdb_score.text = it.rating.imdb.score.toString()
            }
            if (it.countries.data.isEmpty()) {
                tv_files_title_country.text = "N/A"
            } else {
                tv_files_title_country.text = it.countries.data[0].secondaryName
            }
            tv_files_title_name_eng.text = it.secondaryName
            if (it.isTvShow) {
                tv_files_title_duration.text = "${it.seasons!!.data.size} სეზონი"
            } else {
                tv_files_title_duration.text = "${it.duration.toString()} წთ."
            }
            if (it.plot.data.description.isNotEmpty()) {
                tv_files_title_desc.text = it.plot.data.description
            } else {
                tv_files_title_desc.text = "აღწერა არ მოიძებნა"
            }
        })

        if (Firebase.auth.currentUser == null) {
            tvDetailsViewModel.checkContinueWatchingTitleInRoom(requireContext(), titleId).observe(viewLifecycleOwner, { exists ->
                if (exists) {
                    tvDetailsViewModel.getSingleContinueWatchingFromRoom(requireContext(), titleId)
                }
            })
        } else {
            tvDetailsViewModel.checkContinueWatchingInFirestore(titleId)
        }


        tv_files_title_delete.setOnClickListener {
            tvDetailsViewModel.deleteSingleContinueWatchingFromRoom(requireContext(), titleId)
        }

        tvDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                tv_continue_play_button.setOnClickListener { _ ->
                    continueTitlePlay(it)
                }


                if (isTvShow) {
                    tv_continue_play_button?.text = String.format("განაგრძეთ - ს:${it.season} ე:${it.episode} / %02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                            TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )
                } else {
                    tv_continue_play_button?.text = String.format("განაგრძეთ - %02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                            TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )
                }

                tv_continue_play_button.setVisible()
                tv_continue_play_button.requestFocus()
                tv_play_button.text = "თავიდან ყურება"
                tv_files_title_delete.setVisible()
            } else {
                tv_files_title_delete.setGone()
                tv_play_button.requestFocus()
                tv_continue_play_button.setGone()
            }
        })


        tv_play_button.setOnClickListener {
            playTitleFromStart(titleId, isTvShow)
        }

        tvDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(tv_details_spinner_language, languages) { language ->
                tvDetailsViewModel.getTitleLanguageFiles(language)
            }
        })

        if (isTvShow) {
            tv_details_go_bottom_title.text = "ეპიზოდები და მეტი"
        } else {
            tv_details_go_bottom_title.text = "მსხახიობები და მეტი"
        }

        tv_details_go_bottom.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_down, R.anim.slide_out_top)
                        .replace(R.id.tv_details_fr_nav_host, TvTitleFilesFragment())
                        .show(TvTitleFilesFragment())
                        .commit()
            }
            this.hasFocus = hasFocus
        }
    }

    private fun playTitleTrailer(titleId: Int, isTvShow: Boolean, trailerUrl: String) {
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("titleId", titleId)
        intent.putExtra("isTvShow", isTvShow)
        intent.putExtra("chosenLanguage", "ENG")
        intent.putExtra("chosenSeason", 0)
        intent.putExtra("chosenEpisode", 0)
        intent.putExtra("watchedTime", 0L)
        intent.putExtra("trailerUrl", trailerUrl)
        activity?.startActivity(intent)
    }

    private fun playTitleFromStart(titleId: Int, isTvShow: Boolean) {
        trailerUrl = null
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("titleId", titleId)
        intent.putExtra("isTvShow", isTvShow)
        intent.putExtra("chosenLanguage", tvDetailsViewModel.chosenLanguage.value)
        intent.putExtra("chosenSeason", if (isTvShow) 1 else 0)
        intent.putExtra("chosenEpisode", if (isTvShow) 1 else 0)
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
        intent.putExtra("watchedTime", item.watchedDuration)
        intent.putExtra("trailerUrl", trailerUrl)
        activity?.startActivity(intent)
    }
}