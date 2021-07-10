package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.databinding.FragmentTvVideoPlayerBinding
import com.lukakordzaia.streamflow.datamodels.PlayerDurationInfo
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInfo
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.helpers.videoplayer.MediaPlayerClass
import com.lukakordzaia.streamflow.helpers.videoplayer.VideoPlayerHelpers
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.shared.VideoPlayerViewModel
import com.lukakordzaia.streamflow.ui.tv.main.TvActivity
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity.Companion.VIDEO_DETAILS
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.continue_watching_dialog.*
import kotlinx.android.synthetic.main.fragment_phone_video_player.*
import kotlinx.android.synthetic.main.fragment_tv_video_player.*
import kotlinx.android.synthetic.main.phone_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class TvVideoPlayerFragment : BaseFragment<FragmentTvVideoPlayerBinding>() {
    private val videoPlayerViewModel: VideoPlayerViewModel by viewModel()
    private val buildMediaSource: BuildMediaSource by inject()
    private lateinit var videoPlayerData: VideoPlayerData
    private lateinit var videoPlayerInfo: VideoPlayerInfo

    private lateinit var mediaPlayer: MediaPlayerClass
    private lateinit var player: SimpleExoPlayer
    private var tracker: ProgressTrackerNew? = null

    private var mediaItemsPlayed = 0
    private var episodeHasEnded = false

    private val autoBackPress = object : CountDownTimer(500000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvVideoPlayerBinding
        get() = FragmentTvVideoPlayerBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPlayerData = activity?.intent!!.getParcelableExtra("videoPlayerData") as VideoPlayerData

        if (!videoPlayerData.isTvShow) {
            next_details_title.text = "სხვა დეტალები"
        }

        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)

        mediaPlayer.setPlayerListener(PlayerListeners())

        tv_exo_back.setOnClickListener {
            requireActivity().onBackPressed()
            requireActivity().onBackPressed()
        }

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoPlayerViewModel.getTitleFiles(
                VideoPlayerInfo(videoPlayerData.titleId, videoPlayerData.isTvShow, videoPlayerData.chosenSeason, videoPlayerData.chosenEpisode, videoPlayerData.chosenLanguage)
            )
            videoPlayerViewModel.getSingleTitleData(videoPlayerData.titleId)
        }
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
            if ((requireActivity() as TvVideoPlayerActivity).getCurrentFragment() != VIDEO_DETAILS) {
                if (!tv_title_player.isControllerVisible) {
                    requireActivity().onBackPressed()
                } else {
                    requireActivity().onBackPressed()
                    requireActivity().onBackPressed()
                }
                super.onStop()
            } else {
                super.onStop()
            }
        }
    }

    inner class PlayerListeners: Player.Listener {
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
                phone_next_button?.setGone()
            }

            videoPlayerViewModel.videoPlayerInfo.observe(viewLifecycleOwner, {
                videoPlayerInfo = it
            })

            if (this@TvVideoPlayerFragment::videoPlayerInfo.isInitialized) {
                nextButtonClickListener()
            }

            if (state == Player.STATE_READY) {

                episodeHasEnded = true
                showContinueWatchingDialog()
                tracker = ProgressTrackerNew(player, object :
                    PositionListener {
                    override fun progress(position: Long) {
                        exo_live_duration?.text = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(position),
                            TimeUnit.MILLISECONDS.toMinutes(position) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(position)),
                            TimeUnit.MILLISECONDS.toSeconds(position) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))
                        )
                    }
                })
                binding.tvTitlePlayer.keepScreenOn = true
            } else binding.tvTitlePlayer.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)

            if (state == Player.STATE_ENDED) {
                if (episodeHasEnded) {
                    mediaItemsPlayed++
                    tv_next_button?.callOnClick()
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

    private fun nextButtonClickListener() {
        val nextButton = tv_next_button

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
                        mediaPlayer.initPlayer(binding.tvTitlePlayer, 0, 0L)
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
                        mediaPlayer.initPlayer(binding.tvTitlePlayer, 0, 0L)
                    }
                }
            })
        } else {
            nextButton?.setGone()
        }
    }

    private fun setTitleName() {
        if (videoPlayerData.trailerUrl != null) {
            tv_header_tv?.text = "ტრეილერი"
        } else {
            videoPlayerViewModel.setTitleName.observe(viewLifecycleOwner, { name ->
                if (videoPlayerInfo.isTvShow) {
                    tv_header_tv?.text = "ს${videoPlayerInfo.chosenSeason}. ე${videoPlayerInfo.chosenEpisode}. $name"
                } else {
                    tv_header_tv?.text = name
                }
            })
        }
    }

    private fun subtitleFunctions(hasSubs: Boolean) {
//        binding.tvTitlePlayer.subtitleView?.setInvisible()
        VideoPlayerHelpers(requireContext()).subtitleFunctions(binding.tvTitlePlayer.subtitleView!!, tv_subtitle_toggle, player, hasSubs)
    }

    private fun showContinueWatchingDialog() {
        if (mediaItemsPlayed == 4) {
            videoPlayerViewModel.addContinueWatching(requireContext())
            player.pause()

            binding.continueWatching.root.setVisible()

            binding.continueWatching.confirmButton.setOnClickListener {
                binding.continueWatching.root.setGone()
                player.play()
            }

            binding.continueWatching.goBackButton.setOnClickListener {
                val intent = Intent(requireContext(), TvActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(intent)
            }
            binding.continueWatching.confirmButton.requestFocus()

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

                if (it.titleSubUri.isNotEmpty() && it.titleSubUri != "0") {
                    subtitleFunctions(true)
                } else {
                    subtitleFunctions(false)
                }
            })
        }
        mediaPlayer.initPlayer(binding.tvTitlePlayer, 0, watchedTime)
    }

    private fun releasePlayer() {
        mediaPlayer.releasePlayer {
            videoPlayerViewModel.setVideoPlayerInfo(it)
        }
        tracker?.purgeHandler()
        if (videoPlayerData.trailerUrl == null) {
            videoPlayerViewModel.addContinueWatching(requireContext())
        }
    }

    inner class ProgressTrackerNew(private val player: Player, private val positionListener: PositionListener) : Runnable {
        private val handler: Handler = Handler()
        override fun run() {
            val position = player.duration - player.currentPosition
            positionListener.progress(position)
            handler.postDelayed(this, 1000)
        }

        fun purgeHandler() {
            handler.removeCallbacks(this)
        }

        init {
            handler.post(this)
        }
    }

    interface PositionListener {
        fun progress(position: Long)
    }

    override fun onDetach() {
        super.onDetach()
        autoBackPress.cancel()
    }
}