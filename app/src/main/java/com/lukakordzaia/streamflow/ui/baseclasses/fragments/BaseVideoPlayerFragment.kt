package com.lukakordzaia.streamflow.ui.baseclasses.fragments

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
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
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.helpers.videoplayer.MediaPlayerClass
import com.lukakordzaia.streamflow.ui.shared.VideoPlayerViewModel
import com.lukakordzaia.streamflow.utils.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

abstract class BaseVideoPlayerFragment<VB: ViewBinding> : BaseFragmentVM<VB, VideoPlayerViewModel>() {
    override val viewModel by sharedViewModel<VideoPlayerViewModel>()
    private val buildMediaSource: BuildMediaSource by inject()

    protected lateinit var videoPlayerData: VideoPlayerData

    protected lateinit var mediaPlayer: MediaPlayerClass
    protected lateinit var player: SimpleExoPlayer

    protected var mediaItemsPlayed = 0
    private var episodeHasEnded = false

    private var titleName: String = ""
    private var lastEpisode: Int = 0
    private var numOfSeasons: Int = 0

    protected abstract val autoBackPress: AutoBackPress
    private var tracker: ProgressTracker? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPlayerData = requireActivity().intent!!.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData
        setUpPlayer()
        initObservers()

        viewModel.setVideoPlayerData(videoPlayerData)
        playerHasStarted()
        mediaPlayer.setPlayerListener(MediaTransitionListener())
    }

    private fun setUpPlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)
    }

    private fun playerHasStarted() {
        sharedPreferences.saveTvVideoPlayerOn(videoPlayerData.trailerUrl == null)
    }

    private fun initObservers() {
        viewModel.videoPlayerData.observe(viewLifecycleOwner, {
            videoPlayerData = it
        })

        viewModel.totalEpisodesInSeason.observe(viewLifecycleOwner, {
            lastEpisode = it
        })

        viewModel.numOfSeasons.observe(viewLifecycleOwner, {
            numOfSeasons = it
        })

        viewModel.setTitleName.observe(viewLifecycleOwner, {
            titleName = it
        })
    }

    private fun setTitleName(titleView: TextView) {
        titleView.setVisible()
        if (videoPlayerData.trailerUrl != null) {
            titleView.text = getString(R.string.trailer)
        } else {
            if (videoPlayerData.isTvShow) {
                titleView.text = "ს${videoPlayerData.chosenSeason}. ე${videoPlayerData.chosenEpisode}. $titleName"
            } else {
                titleView.text = titleName
            }
        }
    }

    fun initPlayer(player: PlayerView, toggle: ImageButton) {
        if (videoPlayerData.trailerUrl != null) {
            mediaPlayer.setPlayerMediaSource(buildMediaSource.mediaSource(
                TitleMediaItemsUri(Uri.parse(videoPlayerData.trailerUrl), null)
            ))
            subtitleFunctions(player.subtitleView!!, toggle, false)
        } else {
            viewModel.mediaAndSubtitle.observe(viewLifecycleOwner, {
                mediaPlayer.setPlayerMediaSource(buildMediaSource.mediaSource(it))

                subtitleFunctions(player.subtitleView!!, toggle, it.titleSubUri != null)
            })
        }
        mediaPlayer.initPlayer(player, 0, videoPlayerData.watchedTime)
    }

    private fun subtitleFunctions(view: SubtitleView, toggle: ImageButton, hasSubs: Boolean) {
        view.setPadding(0, 0, 0, 20)

        player.addTextOutput {
            view.onCues(it)

            view.setStyle(
                CaptionStyleCompat(
                    ResourcesCompat.getColor(resources, R.color.white, null),
                    ResourcesCompat.getColor(resources, R.color.transparent, null),
                    ResourcesCompat.getColor(resources, R.color.transparent, null),
                    CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                    ResourcesCompat.getColor(resources, R.color.black, null),
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

    private fun nextButtonClickListener(nextButton: ImageButton, view: PlayerView) {
        nextButton.setVisibleOrGone(!(videoPlayerData.chosenSeason == numOfSeasons && videoPlayerData.chosenEpisode == lastEpisode) && videoPlayerData.isTvShow && videoPlayerData.trailerUrl == null)

        nextButton.setOnClickListener {
            nextButtonFunction(videoPlayerData.chosenEpisode == lastEpisode, view)
        }
    }

    private fun nextButtonFunction(lastEpisode: Boolean, view: PlayerView) {
        saveCurrentProgress()

        episodeHasEnded = false
        player.clearMediaItems()
        viewModel.setVideoPlayerData(VideoPlayerData(
            videoPlayerData.titleId,
            videoPlayerData.isTvShow,
            if (lastEpisode) videoPlayerData.chosenSeason+1 else videoPlayerData.chosenSeason,
            videoPlayerData.chosenLanguage,
            if (lastEpisode) 1 else videoPlayerData.chosenEpisode+1,
            0L,
            null
        ))
        mediaPlayer.initPlayer(view, 0, 0L)
    }

    fun saveCurrentProgress() {
        if (videoPlayerData.trailerUrl == null) {
            viewModel.addContinueWatching(player.currentPosition, player.duration)
        }
    }

    fun releasePlayer() {
        mediaPlayer.releasePlayer {
            if (videoPlayerData.trailerUrl == null) {
                viewModel.addContinueWatching(it.playbackPosition, it.titleDuration)
            }
        }
    }

    private fun showContinueWatchingDialog(continueWatching: ContinueWatchingDialogBinding) {
        if (mediaItemsPlayed == 3) {
            viewModel.addContinueWatching(player.currentPosition, player.duration)
            player.pause()

            continueWatching.root.setVisible()

            continueWatching.confirmButton.setOnClickListener {
                continueWatching.root.setGone()
                player.play()
            }

            continueWatching.goBackButton.setOnClickListener {
                requireActivity().onBackPressed()
            }
            continueWatching.confirmButton.requestFocus()

            mediaItemsPlayed = 0
        }
    }

    fun baseStateReady(
        titleView: TextView,
        continueWatching: ContinueWatchingDialogBinding,
        duration: TextView,
        nextButton: ImageButton,
        playerView: PlayerView
    ) {
        setTitleName(titleView)
        showContinueWatchingDialog(continueWatching)
        nextButtonClickListener(nextButton, playerView)
        episodeHasEnded = true

        tracker = ProgressTracker(player) {
            duration.text = it.videoPlayerPosition()
        }
    }

    fun baseStateEnded(nextButton: ImageButton, titleView: TextView) {
        nextButton.setInvisible()
        titleView.setInvisible()
        if (episodeHasEnded) {
            if (videoPlayerData.isTvShow) {
                if (!(videoPlayerData.chosenSeason == numOfSeasons && videoPlayerData.chosenEpisode == lastEpisode)) {
                    mediaItemsPlayed++
                    nextButton.callOnClick()
                } else {
                    requireActivity().onBackPressed()
                }
            } else {
                requireActivity().onBackPressed()
            }
        }
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

    override fun onDetach() {
        tracker?.purgeHandler()
        autoBackPress.cancel()

        super.onDetach()
    }

    inner class MediaTransitionListener: Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            if (isPlaying) autoBackPress.cancel() else autoBackPress.start()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (mediaItem != null) {
                    if (videoPlayerData.trailerUrl == null) {
                        viewModel.addContinueWatching(player.currentPosition, player.duration)
                    }
                }
            }, 2000)
        }
    }

    inner class AutoBackPress(private val backPress: () -> Unit): CountDownTimer(500000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            backPress.invoke()
        }
    }

    inner class ProgressTracker(private val player: Player, private val progress: (position: Long) -> Unit) : Runnable {
        private val handler: Handler = Handler(Looper.myLooper()!!)
        override fun run() {
            val position = if (player.duration <= 0) 0 else player.duration - player.currentPosition
            progress(position)
            handler.postDelayed(this, 1000)
        }

        fun purgeHandler() {
            handler.removeCallbacks(this)
        }

        init {
            handler.post(this)
        }
    }
}