package com.lukakordzaia.streamflow.ui.tv.details.titledetails

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.details.titlefiles.TvTitleFilesFragment
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.tv_choose_language_dialog.*
import kotlinx.android.synthetic.main.tv_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class TvDetailsFragment : Fragment(R.layout.tv_details_fragment) {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()
    private lateinit var tvChooseLanguageAdapter: TvChooseLanguageAdapter
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
        tvDetailsViewModel.checkTitleInFirestore(titleId)

        // For Languages
        tvDetailsViewModel.getSingleTitleFiles(titleId)

        tvDetailsViewModel.traktFavoriteLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    tv_files_title_favorite_progressBar.setVisible()
                    tv_files_title_favorite_icon.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    tv_files_title_favorite_progressBar.setGone()
                    tv_files_title_favorite_icon.setVisible()
                }
            }
        })

        tvDetailsViewModel.addToFavorites.observe(viewLifecycleOwner, {
            if (it) {
                tv_files_title_favorite_icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
                tv_files_title_favorite.setOnClickListener {
                    tvDetailsViewModel.removeTitleFromFirestore(titleId)
                }
            } else {
                tv_files_title_favorite_icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
                tv_files_title_favorite.setOnClickListener {
                    tvDetailsViewModel.addTitleToFirestore()
                }
            }
        })

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
            }
        })

        tvDetailsViewModel.singleTitleData.observe(viewLifecycleOwner, {

            tv_files_trailer.setOnClickListener { _ ->
                if (!it.trailers.data.isNullOrEmpty()) {
                    trailerUrl = it.trailers.data.last().fileUrl
                    playTitleTrailer(titleId, isTvShow, trailerUrl!!)
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
                tv_files_title_duration.text = "${it.seasons?.data?.size} სეზონი"
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

        tvDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                tv_files_title_delete.setOnClickListener {
                    val clearDbDialog = Dialog(requireContext())
                    clearDbDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
                    clearDbDialog.clear_db_alert_yes.setOnClickListener {
                        tvDetailsViewModel.deleteSingleContinueWatchingFromRoom(requireContext(), titleId)
                        tvDetailsViewModel.deleteSingleContinueWatchingFromFirestore(titleId)

                        val intent = Intent(requireContext(), TvDetailsActivity::class.java)
                        intent.putExtra("titleId", titleId)
                        intent.putExtra("isTvShow", isTvShow)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    clearDbDialog.clear_db_alert_no.setOnClickListener {
                        clearDbDialog.dismiss()
                    }
                    clearDbDialog.show()
                    clearDbDialog.clear_db_alert_yes.requestFocus()
                }

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
            val chooseLanguageDialog = Dialog(requireContext())
            chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            chooseLanguageDialog.setContentView(layoutInflater.inflate(R.layout.tv_choose_language_dialog, null))
            chooseLanguageDialog.show()

            val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
            tvChooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) {
                playTitleFromStart(titleId, isTvShow, it)
            }
            chooseLanguageDialog.rv_tv_choose_language.layoutManager = chooseLanguageLayout
            chooseLanguageDialog.rv_tv_choose_language.adapter = tvChooseLanguageAdapter

            tvDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
                val languages = it.reversed()
                tvChooseLanguageAdapter.setLanguageList(languages)
            })
        }

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

        tvDetailsViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
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

    private fun playTitleFromStart(titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        trailerUrl = null
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("titleId", titleId)
        intent.putExtra("isTvShow", isTvShow)
        intent.putExtra("chosenLanguage", chosenLanguage)
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