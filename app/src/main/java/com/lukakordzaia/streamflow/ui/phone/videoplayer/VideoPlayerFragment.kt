package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.VideoPlayerAnimations
import com.lukakordzaia.streamflow.databinding.FragmentPhoneVideoPlayerBinding
import com.lukakordzaia.streamflow.datamodels.PlayerDurationInfo
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInfo
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.helpers.videoplayer.MediaPlayerClass
import com.lukakordzaia.streamflow.helpers.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setInvisible
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.continue_watching_dialog.*
import kotlinx.android.synthetic.main.fragment_phone_video_player.*
import kotlinx.android.synthetic.main.phone_exoplayer_controller_layout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerFragment : BaseFragment<FragmentPhoneVideoPlayerBinding>() {
    private val videoPlayerViewModel: VideoPlayerViewModel by viewModel()
    private val buildMediaSource: BuildMediaSource by inject()
    private lateinit var videoPlayerData: VideoPlayerData
    private lateinit var videoPlayerInfo: VideoPlayerInfo

    private lateinit var mediaPlayer: MediaPlayerClass
    private lateinit var player: SimpleExoPlayer

    private var mediaItemsPlayed = 0
    private var episodeHasEnded = false

    private val autoBackPress = object : CountDownTimer(500000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            requireActivity().onBackPressed()
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneVideoPlayerBinding
        get() = FragmentPhoneVideoPlayerBinding::inflate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPlayerData = activity?.intent!!.getParcelableExtra("videoPlayerData") as VideoPlayerData

        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)

        mediaPlayer.setPlayerListener(PlayerListeners())

        phone_exo_back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (videoPlayerData.trailerUrl == null) {
            videoPlayerViewModel.getTitleFiles(
                VideoPlayerInfo(videoPlayerData.titleId,  videoPlayerData.isTvShow,  videoPlayerData.chosenSeason, videoPlayerData.chosenEpisode, videoPlayerData.chosenLanguage)
            )
            videoPlayerViewModel.getSingleTitleData(videoPlayerData.titleId)
        }

        videoPlayerViewModel.videoPlayerInfo.observe(viewLifecycleOwner, {
            videoPlayerInfo = it
        })
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer(videoPlayerData.watchedTime, videoPlayerData.trailerUrl)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer(videoPlayerData.watchedTime, videoPlayerData.trailerUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
        requireActivity().onBackPressed()
        super.onStop()
    }

    inner class PlayerListeners : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            if (isPlaying) {
                autoBackPress.cancel()
            } else {
                autoBackPress.start()
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            if (videoPlayerData.trailerUrl != null) {
                phone_prev_button?.setGone()
                phone_next_button?.setGone()
            }

            if (this@VideoPlayerFragment::videoPlayerInfo.isInitialized) {
                nextButtonClickListener()
                prevButtonClickListener()
            }

            if (state == Player.STATE_READY) {
                episodeHasEnded = true
                showContinueWatchingDialog()
                binding.phoneTitlePlayer.keepScreenOn = true
            } else binding.phoneTitlePlayer.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)

            if (state == Player.STATE_ENDED) {
                if (episodeHasEnded) {
                    mediaItemsPlayed++
                    phone_next_button?.callOnClick()
                    episodeHasEnded = false
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Handler(Looper.getMainLooper()).postDelayed({
                setTitleName()

                videoPlayerViewModel.setVideoPlayerInfo(
                    PlayerDurationInfo(
                        player.currentPosition,
                        player.duration
                    )
                )
                if (mediaItem != null) {
                    if (videoPlayerData.trailerUrl == null) {
                        videoPlayerViewModel.addContinueWatching(requireContext())
                    }
                }
            }, 2000)
        }
    }

    private fun prevButtonClickListener() {
        val prevButton: ImageButton? = phone_prev_button

        if (videoPlayerInfo.isTvShow) {
            if (videoPlayerInfo.chosenEpisode == 1) {
                prevButton?.setOnClickListener {
                    mediaItemsPlayed = 0
                    exo_prev?.callOnClick()
                }
            } else {
                prevButton?.setOnClickListener {
                    mediaItemsPlayed = 0
                    player.clearMediaItems()
                    videoPlayerViewModel.getTitleFiles(VideoPlayerInfo(
                        videoPlayerInfo.titleId,
                        videoPlayerInfo.isTvShow,
                        videoPlayerInfo.chosenSeason,
                        videoPlayerInfo.chosenEpisode-1,
                        videoPlayerInfo.chosenLanguage
                    ))
                    mediaPlayer.initPlayer(binding.phoneTitlePlayer, 0, 0L)
                }
            }
        } else {
            prevButton?.setGone()
        }
    }

    private fun nextButtonClickListener() {
        val nextButton = phone_next_button

        if (videoPlayerInfo.isTvShow) {
            videoPlayerViewModel.totalEpisodesInSeason.observe(viewLifecycleOwner, {
                if (videoPlayerInfo.chosenEpisode == it) {
                    nextButton?.setOnClickListener {
                        episodeHasEnded = false
                        player.clearMediaItems()
                        videoPlayerViewModel.getTitleFiles(VideoPlayerInfo(
                            videoPlayerInfo.titleId,
                            videoPlayerInfo.isTvShow,
                            videoPlayerInfo.chosenSeason+1,
                            1,
                            videoPlayerInfo.chosenLanguage
                        ))
                        mediaPlayer.initPlayer(binding.phoneTitlePlayer, 0, 0L)
                    }
                } else {
                    nextButton?.setOnClickListener {
                        episodeHasEnded = false
                        player.clearMediaItems()
                        videoPlayerViewModel.getTitleFiles(VideoPlayerInfo(
                            videoPlayerInfo.titleId,
                            videoPlayerInfo.isTvShow,
                            videoPlayerInfo.chosenSeason,
                            videoPlayerInfo.chosenEpisode+1,
                            videoPlayerInfo.chosenLanguage
                        ))
                        mediaPlayer.initPlayer(binding.phoneTitlePlayer, 0, 0L)
                    }
                }
            })
        } else {
            nextButton?.setGone()
        }
    }

    private fun setTitleName() {
        if (videoPlayerData.trailerUrl != null) {
            header_tv?.text = "ტრეილერი"
        } else {
            videoPlayerViewModel.setTitleName.observe(viewLifecycleOwner, { name ->
                if (videoPlayerInfo.isTvShow) {
                    header_tv?.text = "ს${videoPlayerInfo.chosenSeason}. ე${videoPlayerInfo.chosenEpisode}. $name"
                } else {
                    header_tv?.text = name
                }
            })
        }
    }

    private fun showContinueWatchingDialog() {
        if (mediaItemsPlayed == 3) {
            videoPlayerViewModel.addContinueWatching(requireContext())
            player.pause()

            binding.continueWatching.root.setVisible()

            binding.continueWatching.confirmButton.setOnClickListener {
                binding.continueWatching.root.setGone()
                player.play()
            }

            binding.continueWatching.goBackButton.setOnClickListener {
                requireActivity().onBackPressed()
            }

            mediaItemsPlayed = 0
        }
    }

    private fun initPlayer(watchedTime: Long, trailerUrl: String?) {
        if (trailerUrl != null) {
            mediaPlayer.setMediaItems(listOf(MediaItem.fromUri(Uri.parse(trailerUrl))))
            mediaPlayer.setPlayerMediaSource(buildMediaSource.movieMediaSource(
                TitleMediaItemsUri(MediaItem.fromUri(
                    Uri.parse(trailerUrl)), "0")
            ))
        } else {
            videoPlayerViewModel.mediaAndSubtitle.observe(viewLifecycleOwner, {
                mediaPlayer.setPlayerMediaSource(buildMediaSource.movieMediaSource(it))

                if (it.titleSubUri.isNotEmpty()) {
                    if (it.titleSubUri == "0") {
                        subtitleFunctions(false)
                    } else {
                        subtitleFunctions(true)
                    }
                } else {
                    subtitleFunctions(false)
                }
            })
        }
        mediaPlayer.initPlayer(binding.phoneTitlePlayer, 0, watchedTime)
    }

    fun releasePlayer() {
        mediaPlayer.releasePlayer {
            videoPlayerViewModel.setVideoPlayerInfo(it)
        }

        if (videoPlayerData.trailerUrl == null) {
            videoPlayerViewModel.addContinueWatching(requireContext())
        }
    }

    private fun subtitleFunctions(hasSubs: Boolean) {
        binding.phoneTitlePlayer.subtitleView?.apply {
            setInvisible()
        }

        val subtitleView = binding.phoneSubtitle
        val subtitleToggle = phone_subtitle_toggle

        player.addTextOutput {
            subtitleView.onCues(it)

            if (view != null) {
                subtitleView.setStyle(
                    CaptionStyleCompat(
                        ContextCompat.getColor(requireContext(), R.color.white),
                        ContextCompat.getColor(requireContext(), R.color.transparent),
                        ContextCompat.getColor(requireContext(), R.color.transparent),
                        CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                        ContextCompat.getColor(requireContext(), R.color.black),
                        Typeface.DEFAULT_BOLD,
                    )
                )
                subtitleView.setFixedTextSize(2, 25F)
            }
        }

        if (hasSubs) {
            subtitleToggle?.setVisible()
            subtitleToggle?.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.accent_color
                )
            )
        } else {
            subtitleToggle?.setGone()
        }

        subtitleToggle?.setOnClickListener {
            if (subtitleView.isVisible) {
                subtitleView.setInvisible()
                VideoPlayerAnimations().setSubtitleOff(subtitleToggle, 200, requireContext())
            } else {
                subtitleView.setVisible()
                VideoPlayerAnimations().setSubtitleOn(subtitleToggle, 200, requireContext())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    override fun onDetach() {
        super.onDetach()
        autoBackPress.cancel()
    }
}