package com.lukakordzaia.streamflow.ui.baseclasses.fragments

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.VideoPlayerAnimations
import com.lukakordzaia.streamflow.databinding.ContinueWatchingDialogBinding
import com.lukakordzaia.streamflow.datamodels.PlayerDurationInfo
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInfo
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.helpers.videoplayer.MediaPlayerClass
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.ui.shared.VideoPlayerViewModel
import com.lukakordzaia.streamflow.utils.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

abstract class BaseVideoPlayerFragment<VB: ViewBinding> : Fragment() {
    protected val videoPlayerViewModel: VideoPlayerViewModel by sharedViewModel()
    protected val sharedPreferences: SharedPreferences by inject()
    protected val buildMediaSource: BuildMediaSource by inject()

    private var _binding: VB? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    protected val binding: VB
        get() = _binding as VB

    protected lateinit var videoPlayerData: VideoPlayerData
    protected lateinit var videoPlayerInfo: VideoPlayerInfo

    protected lateinit var mediaPlayer: MediaPlayerClass
    protected lateinit var player: SimpleExoPlayer

    protected var mediaItemsPlayed = 0
    protected var episodeHasEnded = false

    protected fun autoBackPress(backPress: () -> Unit) = object : CountDownTimer(500000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            backPress.invoke()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPlayerData = requireActivity().intent!!.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData

        initObservers()
        playerHasStarted()
        setUpPlayer()
        mediaPlayer.setPlayerListener(MediaTransitionListener())
    }

    private fun initObservers() {
        videoPlayerViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            requireActivity().findViewById<ConstraintLayout>(R.id.no_internet).setVisibleOrGone(it)
        })
    }

    private fun playerHasStarted() {
        if (videoPlayerData.trailerUrl == null) {
            sharedPreferences.saveTvVideoPlayerOn(true)
        }
    }

    private fun setUpPlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)

        if (videoPlayerData.trailerUrl == null) {
            videoPlayerViewModel.getTitleFiles(
                VideoPlayerInfo(
                    videoPlayerData.titleId,
                    videoPlayerData.isTvShow,
                    videoPlayerData.chosenSeason,
                    videoPlayerData.chosenEpisode,
                    videoPlayerData.chosenLanguage
                )
            )
            videoPlayerViewModel.getSingleTitleData(videoPlayerData.titleId)
        }
    }

    fun setTitleName(titleView: TextView) {
        if (videoPlayerData.trailerUrl != null) {
            titleView.text = "ტრეილერი"
        } else {
            videoPlayerViewModel.setTitleName.observe(viewLifecycleOwner, { name ->
                if (videoPlayerInfo.isTvShow) {
                    titleView.text = "ს${videoPlayerInfo.chosenSeason}. ე${videoPlayerInfo.chosenEpisode}. $name"
                } else {
                    titleView.text = name
                }
            })
        }
    }

    fun subtitleFunctions(view: SubtitleView, toggle: ImageButton, hasSubs: Boolean) {
        view.setPadding(0, 0, 0, 20)

        player.addTextOutput {
            view.onCues(it)

            view.setStyle(
                CaptionStyleCompat(
                    ContextCompat.getColor(requireContext(), R.color.white),
                    ContextCompat.getColor(requireContext(), R.color.transparent),
                    ContextCompat.getColor(requireContext(), R.color.transparent),
                    CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                    ContextCompat.getColor(requireContext(), R.color.black),
                    Typeface.DEFAULT_BOLD,
                )
            )
            view.setFixedTextSize(2, 25F)
        }

        if (hasSubs) {
            toggle.setVisible()
            toggle.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.accent_color
                )
            )
        } else {
            toggle.setGone()
        }

        toggle.setOnClickListener {
            if (view.isVisible) {
                view.setInvisible()
                VideoPlayerAnimations().setSubtitleOff(toggle, 200, requireContext())
            } else {
                view.setVisible()
                VideoPlayerAnimations().setSubtitleOn(toggle, 200, requireContext())
            }
        }
    }

    fun nextButtonClickListener(nextButton: ImageButton, view: PlayerView) {
        if (videoPlayerInfo.isTvShow) {
            videoPlayerViewModel.totalEpisodesInSeason.observe(viewLifecycleOwner, { lastEpisode ->
                videoPlayerViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                    nextButton.setVisibleOrGone(!(numOfSeasons == videoPlayerInfo.chosenSeason && videoPlayerInfo.chosenEpisode == lastEpisode))
                })

                nextButton.setOnClickListener {
                    nextButtonFunction(videoPlayerInfo.chosenEpisode == lastEpisode, view)
                }
            })
        } else {
            nextButton.setGone()
        }
    }

    private fun nextButtonFunction(lastEpisode: Boolean, view: PlayerView) {
        saveCurrentProgress()

        episodeHasEnded = false
        player.clearMediaItems()
        videoPlayerViewModel.getTitleFiles(VideoPlayerInfo(
            videoPlayerInfo.titleId,
            videoPlayerInfo.isTvShow,
            if (lastEpisode) videoPlayerInfo.chosenSeason+1 else videoPlayerInfo.chosenSeason,
            if (lastEpisode) 1 else videoPlayerInfo.chosenEpisode+1,
            videoPlayerInfo.chosenLanguage
        ))
        mediaPlayer.initPlayer(view, 0, 0L)
    }

    fun saveCurrentProgress() {
        videoPlayerViewModel.setVideoPlayerInfo(
            PlayerDurationInfo(
                player.currentPosition,
                player.duration
            )
        )

        if (videoPlayerData.trailerUrl == null) {
            videoPlayerViewModel.addContinueWatching()
        }
    }

    fun stateHasEnded(nextButton: ImageButton) {
        if (episodeHasEnded) {
            if (videoPlayerInfo.isTvShow) {
                videoPlayerViewModel.totalEpisodesInSeason.observe(viewLifecycleOwner, { lastEpisode ->
                    videoPlayerViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                        if (!(numOfSeasons == videoPlayerInfo.chosenSeason && videoPlayerInfo.chosenEpisode == lastEpisode)) {
                            mediaItemsPlayed++
                            nextButton.callOnClick()
                            episodeHasEnded = false
                        } else {
                            requireActivity().onBackPressed()
                        }
                    })
                })
            } else {
                requireActivity().onBackPressed()
            }
        }
    }

    fun releasePlayer() {
        mediaPlayer.releasePlayer {
            videoPlayerViewModel.setVideoPlayerInfo(it)
        }

        if (videoPlayerData.trailerUrl == null) {
            videoPlayerViewModel.addContinueWatching()
        }
    }

    fun showContinueWatchingDialog(continueWatching: ContinueWatchingDialogBinding, stop: () -> Unit) {
        if (mediaItemsPlayed == 3) {
            videoPlayerViewModel.addContinueWatching()
            player.pause()

            continueWatching.root.setVisible()

            continueWatching.confirmButton.setOnClickListener {
                continueWatching.root.setGone()
                player.play()
            }

            continueWatching.goBackButton.setOnClickListener {
                stop.invoke()
            }
            continueWatching.confirmButton.requestFocus()

            mediaItemsPlayed = 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        if (Util.SDK_INT >= 24) {
            requireActivity().onBackPressed()
        }
        super.onPause()
    }

    override fun onStop() {
        if (Util.SDK_INT >= 24) {
            requireActivity().onBackPressed()
        }
        super.onStop()
    }

    inner class MediaTransitionListener: Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Handler(Looper.getMainLooper()).postDelayed({

                videoPlayerViewModel.setVideoPlayerInfo(
                    PlayerDurationInfo(
                        player.currentPosition,
                        player.duration
                    )
                )
                if (mediaItem != null) {
                    if (videoPlayerData.trailerUrl == null) {
                        videoPlayerViewModel.addContinueWatching()
                    }
                }
            }, 2000)
        }
    }
}