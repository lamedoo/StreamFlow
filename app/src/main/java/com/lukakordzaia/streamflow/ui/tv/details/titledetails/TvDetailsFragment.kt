package com.lukakordzaia.streamflow.ui.tv.details.titledetails

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflow.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflow.databinding.FragmentTvDetailsBinding
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.details.titlefiles.TvTitleFilesFragment
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class TvDetailsFragment : BaseFragment<FragmentTvDetailsBinding>() {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()
    private lateinit var tvChooseLanguageAdapter: TvChooseLanguageAdapter
    private var trailerUrl: String? = null
    private var hasFocus: Boolean = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvDetailsBinding
        get() = FragmentTvDetailsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        fragmentListeners(titleId, isTvShow)
        fragmentObservers(titleId)
        favoriteContainer(titleId, isTvShow)
        titleDetails(titleId, isTvShow)
    }

    private fun fragmentListeners(titleId: Int, isTvShow: Boolean) {
        binding.playButton.setOnClickListener {
            val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
            val chooseLanguageDialog = Dialog(requireContext())
            chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            chooseLanguageDialog.setContentView(binding.root)
            chooseLanguageDialog.show()

            val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
            tvChooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) {
                playTitleFromStart(titleId, isTvShow, it)
            }
            binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
            binding.rvChooseLanguage.adapter = tvChooseLanguageAdapter

            tvDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
                val languages = it.reversed()
                tvChooseLanguageAdapter.setLanguageList(languages)
            })
        }

        if (isTvShow) {
            binding.nextDetailsTitle.text = "ეპიზოდები და მეტი"
        } else {
            binding.nextDetailsTitle.text = "მსხახიობები და მეტი"
        }

        binding.nextDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_from_down, R.anim.slide_out_top)
                    .replace(R.id.tv_details_fr_nav_host, TvTitleFilesFragment())
                    .commit()
            }
            this.hasFocus = hasFocus
        }
    }

    private fun fragmentObservers(titleId: Int) {
        tvDetailsViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        tvDetailsViewModel.traktFavoriteLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.favoriteProgressBar.setVisible()
                    binding.favoriteIcon.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.favoriteProgressBar.setGone()
                    binding.favoriteIcon.setVisible()
                }
            }
        })

        tvDetailsViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    tvDetailsViewModel.getSingleTitleData(titleId)
                    tvDetailsViewModel.getSingleTitleFiles(titleId)
                }, 5000)
            }
        })

        tvDetailsViewModel.dataLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> binding.tvDetailsProgressBar.setVisible()
                LoadingState.LOADED -> {
                    binding.tvDetailsProgressBar.setGone()
                    binding.mainContainer.setVisible()
                }
            }
        })

        tvDetailsViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                binding.noFilesContainer.setGone()
                binding.buttonsRow.setVisible()
            }
        })
    }

    private fun favoriteContainer(titleId: Int, isTvShow: Boolean) {
        tvDetailsViewModel.checkTitleInFirestore(titleId)

        tvDetailsViewModel.addToFavorites.observe(viewLifecycleOwner, {
            if (it) {
                binding.favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
                binding.favoriteContainer.setOnClickListener {
                    tvDetailsViewModel.removeTitleFromFirestore(titleId)
                }
            } else {
                binding.favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
                binding.favoriteContainer.setOnClickListener {
                    tvDetailsViewModel.addTitleToFirestore()
                }
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
                binding.deleteButton.setOnClickListener {
                    val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
                    val removeTitle = Dialog(requireContext())
                    removeTitle.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    removeTitle.setContentView(binding.root)

                    binding.continueButton.setOnClickListener {
                        tvDetailsViewModel.deleteSingleContinueWatchingFromRoom(requireContext(), titleId)
                        tvDetailsViewModel.deleteSingleContinueWatchingFromFirestore(titleId)

                        val intent = Intent(requireContext(), TvDetailsActivity::class.java)
                        intent.putExtra("titleId", titleId)
                        intent.putExtra("isTvShow", isTvShow)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    binding.cancelButton.setOnClickListener {
                        removeTitle.dismiss()
                    }
                    removeTitle.show()
                    binding.continueButton.requestFocus()
                }

                binding.continueButton.setOnClickListener { _ ->
                    continueTitlePlay(it)
                }

                if (isTvShow) {
                    binding.continueButton.text = String.format("განაგრძეთ - ს:${it.season} ე:${it.episode} / %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                        TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )
                } else {
                    binding.continueButton.text = String.format("განაგრძეთ - %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                        TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )
                }

                binding.continueButton.setVisible()
                binding.continueButton.requestFocus()
                binding.playButton.text = "თავიდან ყურება"
                binding.deleteButton.setVisible()
            } else {
                binding.deleteButton.setGone()
                binding.playButton.requestFocus()
                binding.continueButton.setGone()
            }
        })
    }

    private fun titleDetails(titleId: Int, isTvShow: Boolean) {
        tvDetailsViewModel.getSingleTitleData(titleId)
        tvDetailsViewModel.getSingleTitleFiles(titleId)

        tvDetailsViewModel.singleTitleData.observe(viewLifecycleOwner, {
            binding.trailerButton.setOnClickListener { _ ->
                if (!it.trailers.data.isNullOrEmpty()) {
                    trailerUrl = it.trailers.data.last().fileUrl
                    playTitleTrailer(titleId, isTvShow, trailerUrl!!)
                } else {
                    requireContext().createToast("no trailer")
                }
            }

            if (it.covers?.data?.x1050!!.isNotBlank()) {
                Picasso.get().load(it.covers.data.x1050).error(R.drawable.movie_image_placeholder_landscape).into(binding.backgroundPoster)
            } else {
                Picasso.get().load(R.drawable.movie_image_placeholder_landscape).error(R.drawable.movie_image_placeholder_landscape).into(binding.backgroundPoster)
            }

            binding.year.text = it.year.toString()
            if (it.rating.imdb?.score != null) {
                binding.imdbScore.text = it.rating.imdb.score.toString()
            }
            if (it.countries.data.isEmpty()) {
                binding.country.text = "N/A"
            } else {
                binding.country.text = it.countries.data[0].secondaryName
            }
            binding.titleName.text = it.secondaryName
            if (it.isTvShow) {
                binding.duration.text = "${it.seasons?.data?.size} სეზონი"
            } else {
                binding.duration.text = "${it.duration.toString()} წთ."
            }
            if (it.plot.data.description.isNotEmpty()) {
                binding.titleDescription.text = it.plot.data.description
            } else {
                binding.titleDescription.text = "აღწერა არ მოიძებნა"
            }
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