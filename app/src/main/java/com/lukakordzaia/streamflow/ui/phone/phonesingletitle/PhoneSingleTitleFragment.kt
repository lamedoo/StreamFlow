package com.lukakordzaia.streamflow.ui.phone.phonesingletitle

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleTitleBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.tvshowdetailsbottomsheet.TvShowBottomSheetViewModel
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.ChooseLanguageAdapter
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class PhoneSingleTitleFragment : BaseFragment<FragmentPhoneSingleTitleBinding>() {
    private val phoneSingleTitleViewModel: PhoneSingleTitleViewModel by viewModel()
    private val tvShowBottomSheetViewModel: TvShowBottomSheetViewModel by viewModel()
    private lateinit var titleInfo: SingleTitleModel
    private lateinit var chooseLanguageAdapter: ChooseLanguageAdapter
    private lateinit var phoneSingleTitleCastAdapter: PhoneSingleTitleCastAdapter
    private lateinit var phoneSingleTitleRelatedAdapter: PhoneSingleTitleRelatedAdapter
    private val args: PhoneSingleTitleFragmentArgs by navArgs()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleTitleBinding
        get() = FragmentPhoneSingleTitleBinding::inflate

    override fun onStart() {
        super.onStart()
        phoneSingleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${sharedPreferences.getTraktToken()}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.singleTitleAppbar.setExpanded(true)

        checkFavorites()

        fragmentListeners()
        fragmentObservers()
        titleDetailsContainer()
        checkContinueWatching()
        castContainer()
        relatedContainer()
    }

    private fun checkFavorites() {
        phoneSingleTitleViewModel.addToFavorites.observe(viewLifecycleOwner, {
            if (it) {
                binding.singleTitleFavoriteIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_favorite_full, null))
                binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
                binding.singleTitleFavorite.setOnClickListener {
                    phoneSingleTitleViewModel.deleteWatchlistTitle(args.titleId)
                }
            } else {
                binding.singleTitleFavoriteIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_favorite, null))
                binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
                binding.singleTitleFavorite.setOnClickListener {
                    phoneSingleTitleViewModel.addWatchlistTitle(args.titleId)
                }
            }
        })

        phoneSingleTitleViewModel.favoriteLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.singleTitleFavoriteProgressBar.setVisible()
                    binding.singleTitleFavoriteIcon.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.singleTitleFavoriteProgressBar.setGone()
                    binding.singleTitleFavoriteIcon.setVisible()
                }
            }
        })
    }

    private fun fragmentListeners() {
        binding.playButtonBottom.setOnClickListener {
            binding.playButton.callOnClick()
        }

        binding.episodesButtonBottom.setOnClickListener {
            binding.episodesButton.callOnClick()
        }

        binding.replayButtonBottom.setOnClickListener {
            binding.replayButton.callOnClick()
        }

        binding.singleTitleAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) == binding.singleTitleAppbar.totalScrollRange) {
                binding.singleTitleDetailsScroll.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primaryColor
                    )
                )
            } else {
                binding.singleTitleDetailsScroll.background = ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.background_single_title_cover_phone,
                    null
                )
            }
        })

        binding.singleTitleBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun fragmentObservers() {
        phoneSingleTitleViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    phoneSingleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${sharedPreferences.getTraktToken()}")
                }, 5000)
            }
        })

        phoneSingleTitleViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        phoneSingleTitleViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

        binding.singleTitleAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbar, verticalOffset ->
            var offsetPercent = 0F
            var playButtonAlpha = 0F

            try {
                offsetPercent = (abs(verticalOffset).toFloat() * 100F) / (appbar.totalScrollRange.toFloat())
                playButtonAlpha = 1F - ((offsetPercent) / 80F)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            val playButtonBottomAlpha = if (offsetPercent >= 80F) {
                (-4F) + ((offsetPercent) / 20F)
            } else {
                0F
            }

            if (abs(verticalOffset) == appbar.totalScrollRange) {
                binding.playButton.alpha = 0F
                binding.episodesButton.alpha = 0F
                binding.replayButton.alpha = 0F
                binding.continueWatchingInfo.alpha = 0F
                binding.continueWatchingSeekBar.alpha = 0F
                binding.playButtonBottomContainer.alpha = 1F
            } else {
                binding.playButton.alpha = playButtonAlpha
                binding.episodesButton.alpha = playButtonAlpha
                binding.replayButton.alpha = playButtonAlpha
                binding.continueWatchingInfo.alpha = playButtonAlpha
                binding.continueWatchingSeekBar.alpha = playButtonAlpha
                binding.playButtonBottomContainer.alpha = playButtonBottomAlpha
            }
        })
    }

    private fun titleDetailsContainer() {
        phoneSingleTitleViewModel.singleTitleLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.progressBar.setVisible()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.progressBar.setGone()
                    binding.singleTitleMainContainer.setVisible()
                }
            }
        })

        phoneSingleTitleViewModel.getSingleTitleResponse.observe(viewLifecycleOwner, {
            titleInfo = it

            binding.titleName.text = it.displayName

            binding.singleTitleCover.setImage(it.cover, false)

            binding.titleTrailer.setOnClickListener { _ ->
                startTrailer(it)
            }

            binding.titleDescription.text = it.description
            binding.infoDetails.imdbScore.text = "IMDB ${it.imdbScore}"
            binding.infoDetails.year.text = it.releaseYear
            binding.infoDetails.duration.text = it.duration

            if (it.isTvShow) {
                binding.episodesButton.setVisible()
                binding.episodesButtonBottom.setVisible()
                binding.episodesButton.setOnClickListener { _ ->
                    phoneSingleTitleViewModel.onEpisodesPressed(it.id, it.displayName!!, it.seasonNum!!)
                }
            }
        })

        phoneSingleTitleViewModel.titleGenres.observe(viewLifecycleOwner, {
            binding.titleGenre.text = TextUtils.join(", ", it)
        })

        phoneSingleTitleViewModel.getSingleTitleDirectorResponse.observe(viewLifecycleOwner, {
            binding.titleDirector.text = it.originalName
        })
    }

    private fun checkContinueWatching() {
        phoneSingleTitleViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            binding.continueWatchingInfo.setVisibleOrGone(it != null)
            binding.continueWatchingSeekBar.setVisibleOrGone(it != null)

            binding.continueWatchingInfoBottom.setVisibleOrGone(it != null)
            binding.continueWatchingSeekBarBottom.setVisibleOrGone(it != null)

            if (it != null) {
                binding.continueWatchingSeekBar.max = it.titleDuration.toInt()
                binding.continueWatchingSeekBar.progress = it.watchedDuration.toInt()

                binding.continueWatchingSeekBarBottom.max = it.titleDuration.toInt()
                binding.continueWatchingSeekBarBottom.progress = it.watchedDuration.toInt()

                if (it.isTvShow) {
                    val time = String.format(
                        "ს${it.season} ე${it.episode} / %02d:%02d",
                        TimeUnit.SECONDS.toMinutes(it.watchedDuration),
                        TimeUnit.SECONDS.toSeconds(it.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(it.watchedDuration))
                    )

                    binding.continueWatchingInfo.text = time
                    binding.continueWatchingInfoBottom.text = time
                } else {
                    val time = String.format(
                        "%02d:%02d",
                        TimeUnit.SECONDS.toMinutes(it.watchedDuration),
                        TimeUnit.SECONDS.toSeconds(it.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(it.watchedDuration))
                    )

                    binding.continueWatchingInfo.text = time
                    binding.continueWatchingInfoBottom.text = time

                    binding.replayButton.setVisible()
                    binding.replayButtonBottom.setVisible()
                    binding.replayButton.setOnClickListener { _ ->
                        languagePickerDialog()
                    }
                }

                binding.playButton.setOnClickListener { _ ->
                    startVideoPlayer(
                        ContinueWatchingRoom(
                            it.titleId,
                            it.language,
                            TimeUnit.SECONDS.toMillis(it.watchedDuration),
                            TimeUnit.SECONDS.toMillis(it.titleDuration),
                            it.isTvShow,
                            it.season,
                            it.episode
                        ), null)
                }
            } else {
                binding.playButton.setOnClickListener { _ ->
                    languagePickerDialog()
                }
            }
        })
    }

    private fun castContainer() {
        val castLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        phoneSingleTitleCastAdapter = PhoneSingleTitleCastAdapter(requireContext()) {
            requireContext().createToast(it)
        }
        binding.singleTitleCastSimilar.rvSingleTitleCast.layoutManager = castLayout
        binding.singleTitleCastSimilar.rvSingleTitleCast.adapter = phoneSingleTitleCastAdapter

        phoneSingleTitleViewModel.castResponseDataGetSingle.observe(viewLifecycleOwner, {
            phoneSingleTitleCastAdapter.setCastList(it)
        })
    }

    private fun relatedContainer() {
        val relatedLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        phoneSingleTitleRelatedAdapter = PhoneSingleTitleRelatedAdapter(requireContext()) {
            phoneSingleTitleViewModel.onRelatedTitlePressed(it)
        }
        binding.singleTitleCastSimilar.rvSingleTitleRelated.layoutManager = relatedLayout
        binding.singleTitleCastSimilar.rvSingleTitleRelated.adapter = phoneSingleTitleRelatedAdapter

        phoneSingleTitleViewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            phoneSingleTitleRelatedAdapter.setRelatedList(it)
        })
    }

    private fun languagePickerDialog() {
        tvShowBottomSheetViewModel.getSeasonFiles(args.titleId, 1)

        val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        chooseLanguageDialog.setContentView(binding.root)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = ChooseLanguageAdapter(requireContext()) { language ->
            chooseLanguageDialog.hide()
            startVideoPlayer(null, language)
        }
        binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
        binding.rvChooseLanguage.adapter = chooseLanguageAdapter

        tvShowBottomSheetViewModel.availableLanguages.observe(viewLifecycleOwner, { languageList ->
            val languages = languageList.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
        })
    }

    private fun startTrailer(data: SingleTitleModel) {
        if (data.trailer != null) {
            requireActivity().startActivity(VideoPlayerActivity.startFromTrailers(requireContext(), VideoPlayerData(
                data.id,
                false,
                0,
                "ENG",
                0,
                0L,
                data.trailer
            )
            ))
        } else {
            phoneSingleTitleViewModel.newToastMessage("ტრეილერი ვერ მოიძებნა")
        }
    }

    private fun startVideoPlayer(contWatching: ContinueWatchingRoom?, language: String?) {
        requireActivity().startActivity(VideoPlayerActivity.startFromSingleTitle(requireContext(), VideoPlayerData(
            contWatching?.titleId ?: titleInfo.id,
            contWatching?.isTvShow ?: titleInfo.isTvShow,
            contWatching?.season ?: if (titleInfo.isTvShow) 1 else 0,
            contWatching?.language ?: language!!,
            contWatching?.episode ?: if (titleInfo.isTvShow) 1 else 0,
             contWatching?.watchedDuration ?: 0L,
            null
        )
        ))
    }
}