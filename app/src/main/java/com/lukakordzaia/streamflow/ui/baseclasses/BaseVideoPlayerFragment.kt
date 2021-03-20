package com.lukakordzaia.streamflow.ui.baseclasses

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.text.CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SubtitleView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.VideoPlayerAnimations
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInfo
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.helpers.videoplayer.MediaPlayerClass
import com.lukakordzaia.streamflow.helpers.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflow.ui.tv.TvActivity
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.continue_watching_dialog.*
import kotlinx.android.synthetic.main.phone_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.phone_fragment_video_player.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.tv_video_player_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


open class BaseVideoPlayerFragment(fragment: Int) : Fragment(fragment) {
    protected val videoPlayerViewModel: VideoPlayerViewModel by viewModel()
    private val buildMediaSource: BuildMediaSource by inject()

    private lateinit var mediaPlayer: MediaPlayerClass
    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private var tracker: ProgressTracker? = null
    var nextSeasonButton: Button? = null
    private var mediaItemsPlayed = 0
    private var isTrailer: Boolean = false

    private var titleId = 0
    private var isTvShow = false
    private var chosenLanguage = ""

    private val autoBackPress = object : CountDownTimer(500000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                if (this@BaseVideoPlayerFragment.activity is TvVideoPlayerActivity) {
                    requireActivity().onBackPressed()
                    requireActivity().onBackPressed()
                } else {
                    requireActivity().onBackPressed()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)

        mediaPlayer.setPlayerListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)

                if (isPlaying) {
                    checkForNextSeasonOnEnd().start()
                    Log.d("playstate", "playing")
                    autoBackPress.cancel()
                } else {
                    Log.d("playstate", "paused")
                    autoBackPress.start()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)

                if (state == Player.STATE_READY) {
                    tracker = ProgressTracker(player, object : ProgressTracker.PositionListener {
                        override fun progress(position: Long) {
                            exo_live_duration?.text = String.format(
                                "%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(position),
                                TimeUnit.MILLISECONDS.toMinutes(position) -
                                        TimeUnit.HOURS.toMinutes(
                                            TimeUnit.MILLISECONDS.toHours(
                                                position
                                            )
                                        ),
                                TimeUnit.MILLISECONDS.toSeconds(position) -
                                        TimeUnit.MINUTES.toSeconds(
                                            TimeUnit.MILLISECONDS.toMinutes(
                                                position
                                            )
                                        )
                            )

                        }
                    })

                    videoPlayerViewModel.setTitleName.observe(viewLifecycleOwner, { name ->
                        if (isTvShow) {
                            setEpisodeName(name)
                        } else {
                            if (name.isNullOrEmpty()) {
                                setMovieName("ტრეილერი")
                            } else {
                                setMovieName(name[player.currentWindowIndex])
                            }
                        }
                    })

                    checkForNextSeason()
                    playerView.keepScreenOn = true
                } else playerView.keepScreenOn =
                    !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)

                if (state == Player.STATE_ENDED) {
                    Log.d("playing", "false")
                    if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
                        if (isTvShow) {
                            val nextSeasonNum = videoPlayerViewModel.seasonForDb.value!! + 1
                            videoPlayerViewModel.numOfSeasons.observe(
                                viewLifecycleOwner,
                                { numOfSeasons ->
                                    if (nextSeasonNum <= numOfSeasons) {
                                        nextSeasonButtonClickOnEnd(nextSeasonNum)
                                        nextSeasonButton?.callOnClick()
                                    }
                                })
                        }
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaItemsPlayed < 4) {
                    setControllerVisible()
                }

                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    mediaItemsPlayed++
                    Log.d("mediatransition", mediaItemsPlayed.toString())
                    showContinueWatchingDialog()
                } else if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                    mediaItemsPlayed = 0
                    Log.d("mediatransition", mediaItemsPlayed.toString())
                }

                videoPlayerViewModel.mediaAndSubtitle.observe(viewLifecycleOwner, {
                    if (!it.isNullOrEmpty()) {
                        if (it[player.currentWindowIndex].titleSubUri.isNotEmpty()) {
                            if (it[player.currentWindowIndex].titleSubUri == "0") {
                                subtitleFunctions(false)
                            } else {
                                subtitleFunctions(true)
                            }
                        } else {
                            subtitleFunctions(false)
                        }
                    }
                })


                Handler(Looper.getMainLooper()).postDelayed({
                    videoPlayerViewModel.setVideoPlayerInfo(
                        VideoPlayerInfo(
                            player.currentWindowIndex,
                            player.currentPosition,
                            player.duration
                        )
                    )
                    if (!isTrailer) {
                        videoPlayerViewModel.addContinueWatching(requireContext(), titleId, isTvShow, chosenLanguage)
                    }
                }, 2000)
            }
        })

        videoPlayerViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it, Toast.LENGTH_LONG)
        })
    }

    override fun onDetach() {
        super.onDetach()
        checkForNextSeasonOnEnd().cancel()
        autoBackPress.cancel()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun checkForNextSeason() {
        if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
            if (isTvShow) {
                val nextSeasonNum = videoPlayerViewModel.seasonForDb.value!! + 1
                videoPlayerViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                    if (nextSeasonNum <= numOfSeasons) {
                        nextSeasonButtonShow(nextSeasonNum)
                    } else {
                        nextSeasonButtonGone()
                    }
                })
            }
        } else {
            nextSeasonButtonGone()
        }
    }

    private fun checkForNextSeasonOnEnd() : CountDownTimer {
        return object : CountDownTimer(player.duration, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                if (player.currentPosition >= player.duration - 50000L) {
                    if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
                        if (isTvShow) {
                            val nextSeasonNum = videoPlayerViewModel.seasonForDb.value!! + 1
                            if (view != null) {
                                videoPlayerViewModel.numOfSeasons.observe(
                                    viewLifecycleOwner,
                                    { numOfSeasons ->
                                        if (nextSeasonNum <= numOfSeasons) {
                                            nextSeasonButtonShowOnEnd(nextSeasonNum)
                                        } else {
                                            nextSeasonButtonGoneOnEnd()
                                        }
                                    })
                            }
                        }
                    } else {
                        nextSeasonButtonGoneOnEnd()
                    }
                } else {
                    nextSeasonButtonGoneOnEnd()
                }
            }
            override fun onFinish() {
            }
        }
    }

    private fun nextSeasonButtonShowOnEnd(nextSeasonNum: Int) {
        var nextSeasonButton: Button? = null
        when (playerView) {
            tv_title_player -> {
                nextSeasonButton = tv_next_season_button
            }
            phone_title_player -> {
                nextSeasonButton = phone_next_season_button
            }
        }

        nextSeasonButton?.setVisible()
        nextSeasonButton?.requestFocus()
        nextSeasonButton?.text = "შემდეგი სეზონი: $nextSeasonNum"
        nextSeasonButton?.setOnClickListener {
            player.clearMediaItems()
            videoPlayerViewModel.getPlaylistFiles(
                videoPlayerViewModel.titleIdForDb.value!!,
                nextSeasonNum,
                videoPlayerViewModel.languageForDb.value!!
            )
            mediaPlayer.initPlayer(playerView, 0, 0L)

        }
    }

    private fun nextSeasonButtonClickOnEnd(nextSeasonNum: Int) {
        when (playerView) {
            tv_title_player -> {
                nextSeasonButton = tv_next_season_button
            }
            phone_title_player -> {
                nextSeasonButton = phone_next_season_button
            }
        }

        nextSeasonButton?.setVisible()
        nextSeasonButton?.requestFocus()
        nextSeasonButton?.text = "შემდეგი სეზონი: $nextSeasonNum"
        nextSeasonButton?.setOnClickListener {
            player.clearMediaItems()
            videoPlayerViewModel.getPlaylistFiles(
                videoPlayerViewModel.titleIdForDb.value!!,
                nextSeasonNum,
                videoPlayerViewModel.languageForDb.value!!
            )
            mediaPlayer.initPlayer(playerView, 0, 0L)
            playerView.showController()
        }
    }

    private fun nextSeasonButtonShow(nextSeasonNum: Int) {
        var nextSeasonButtonInController: ImageButton? = null
        when (playerView) {
            tv_title_player -> {
                nextSeasonButtonInController = tv_next_season_button_controller
            }
            phone_title_player -> {
                nextSeasonButtonInController = phone_next_season_button_controller
            }
        }

        nextSeasonButtonInController?.setVisible()
        nextSeasonButtonInController?.setOnClickListener {
            player.clearMediaItems()
            videoPlayerViewModel.getPlaylistFiles(
                videoPlayerViewModel.titleIdForDb.value!!,
                nextSeasonNum,
                videoPlayerViewModel.languageForDb.value!!
            )
            mediaPlayer.initPlayer(playerView, 0, 0L)
        }
    }

    private fun nextSeasonButtonGoneOnEnd() {
        tv_next_season_button?.setGone()
        phone_next_season_button?.setGone()
    }

    private fun nextSeasonButtonGone() {
        tv_next_season_button_controller?.setGone()
        phone_next_season_button_controller?.setGone()
    }

    private fun setEpisodeName(names: List<String>) {
        header_tv?.text = "ს${videoPlayerViewModel.seasonForDb.value}. ე${player.currentWindowIndex + 1}. ${names[player.currentWindowIndex]}"
        tv_header_tv?.text = "ს${videoPlayerViewModel.seasonForDb.value}. ე${player.currentWindowIndex + 1}. ${names[player.currentWindowIndex]}"
    }

    private fun setMovieName(name: String) {
        header_tv?.text = name
        tv_header_tv?.text = name
    }

    fun setExoPlayer(playerView: PlayerView) {
        this.playerView = playerView

        playerView.subtitleView?.apply {
            setInvisible()
        }
    }

    private fun setControllerVisible() {
        tv_title_player?.showController()
        phone_title_player?.showController()
    }

    private fun subtitleFunctions(hasSubs: Boolean) {
        var subtitleView: SubtitleView? = null
        var subtitleToggle: ImageButton? = null
        when (playerView) {
            tv_title_player -> {
                subtitleView = tv_subtitle
                subtitleToggle = tv_subtitle_toggle
            }
            phone_title_player -> {
                subtitleView = phone_subtitle
                subtitleToggle = phone_subtitle_toggle
            }
        }

        player.addTextOutput {
            subtitleView?.onCues(it)

            if (view != null) {
                subtitleView?.setStyle(
                    CaptionStyleCompat(
                        ContextCompat.getColor(requireContext(), R.color.white),
                        ContextCompat.getColor(requireContext(), R.color.transparent),
                        ContextCompat.getColor(requireContext(), R.color.transparent),
                        EDGE_TYPE_DROP_SHADOW,
                        ContextCompat.getColor(requireContext(), R.color.black),
                        Typeface.DEFAULT_BOLD,
                    )
                )
                subtitleView?.setFixedTextSize(2, 25F)
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
            subtitleToggle?.setInvisible()
        }

        subtitleToggle?.setOnClickListener {
            if (subtitleView?.isVisible == true) {
                subtitleView.setInvisible()
                VideoPlayerAnimations().setSubtitleOff(subtitleToggle, 200, requireContext())
            } else {
                subtitleView?.setVisible()
                VideoPlayerAnimations().setSubtitleOn(subtitleToggle, 200, requireContext())
            }
        }
    }

    private fun showContinueWatchingDialog() {
        if (mediaItemsPlayed == 4) {
            videoPlayerViewModel.addContinueWatching(
                requireContext(),
                titleId,
                isTvShow,
                chosenLanguage
            )
            player.pause()
            tv_title_player?.controllerShowTimeoutMs = 200

            continue_watching_dialog_root.setVisible()

            continue_watching_dialog_yes.setOnClickListener {
                continue_watching_dialog_root.setGone()
                player.play()
            }

            if (this.activity is TvVideoPlayerActivity) {
                continue_watching_dialog_home.setOnClickListener {
                    val intent = Intent(requireContext(), TvActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(intent)
                }
            } else {
                continue_watching_dialog_home.setOnClickListener {
                    requireActivity().onBackPressed()
                }
            }
            continue_watching_dialog_yes.requestFocus()

            mediaItemsPlayed = 0
        }
    }



    fun getPlayListFiles(titleId: Int, chosenSeason: Int, chosenLanguage: String, isTvShow: Boolean) {
        videoPlayerViewModel.getPlaylistFiles(titleId, chosenSeason, chosenLanguage)
        videoPlayerViewModel.getSingleTitleData(titleId)

        this.titleId = titleId
        this.isTvShow = isTvShow
        this.chosenLanguage = chosenLanguage
    }

    fun initPlayer(isTvShow: Boolean, watchedTime: Long, chosenEpisode: Int, trailerUrl: String?) {
        if (trailerUrl != null) {
            isTrailer = true
            mediaPlayer.setMediaItems(listOf(MediaItem.fromUri(Uri.parse(trailerUrl))))
            mediaPlayer.initPlayer(playerView, chosenEpisode, watchedTime)
        } else {
            isTrailer = false
            videoPlayerViewModel.mediaAndSubtitle.observe(viewLifecycleOwner, { it ->
                if (it.size == 1) {
                    mediaPlayer.setPlayerMediaSource(buildMediaSource.movieMediaSource(it[0]))
                } else if (it.size > 1) {
                    mediaPlayer.setMultipleMediaSources(buildMediaSource.tvShowMediaSource(it))
                }
            })
            mediaPlayer.initPlayer(playerView, if (isTvShow) chosenEpisode - 1 else 0, watchedTime)
        }
    }

    fun releasePlayer(titleId: Int, isTvShow: Boolean, chosenLanguage: String, trailerUrl: String?) {
        mediaPlayer.releasePlayer {
            videoPlayerViewModel.setVideoPlayerInfo(it)
        }
        tracker?.purgeHandler()
        if (trailerUrl == null) {
            videoPlayerViewModel.addContinueWatching(requireContext(), titleId, isTvShow, chosenLanguage)
        }
    }
}

class ProgressTracker(private val player: Player, private val positionListener: PositionListener) : Runnable {
    interface PositionListener {
        fun progress(position: Long)
    }

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