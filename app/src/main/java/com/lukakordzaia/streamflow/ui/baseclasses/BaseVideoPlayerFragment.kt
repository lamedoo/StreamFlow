package com.lukakordzaia.streamflow.ui.baseclasses

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
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
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerInfo
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.helpers.videoplayer.MediaPlayerClass
import com.lukakordzaia.streamflow.helpers.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflow.utils.*
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
    private lateinit var tracker: ProgressTracker

    private var titleId = 0
    private var isTvShow = false
    private var chosenLanguage = ""

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
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)

                if (state == Player.STATE_READY) {
                    tracker = ProgressTracker(player, object: ProgressTracker.PositionListener {
                        override fun progress(position: Long) {
                            exo_live_duration.text = String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(position),
                                    TimeUnit.MILLISECONDS.toMinutes(position) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(position)),
                                    TimeUnit.MILLISECONDS.toSeconds(position) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))
                            )

                        }
                    })
                    checkForNextSeason()
                    playerView.keepScreenOn = true
                } else playerView.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                setControllerVisible()
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

                videoPlayerViewModel.setVideoPlayerInfo(
                        VideoPlayerInfo(
                                player.currentWindowIndex,
                                player.currentPosition,
                                player.duration
                        )
                )
                videoPlayerViewModel.addContinueWatching(requireContext(), titleId, isTvShow, chosenLanguage)
            }
        })

        videoPlayerViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it, Toast.LENGTH_LONG)
        })
    }

    override fun onDetach() {
        super.onDetach()
        checkForNextSeasonOnEnd().cancel()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
        } else {
            requireActivity().window.decorView.systemUiVisibility = View.VISIBLE
        }
    }

    private fun checkForNextSeason() {
        if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
            if (isTvShow) {
                val nextSeasonNum = videoPlayerViewModel.seasonForDb.value!! + 1
                videoPlayerViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                    if (nextSeasonNum <= numOfSeasons) {
                        nextSeasonButtonShow(nextSeasonNum, isTvShow)
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
                                videoPlayerViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                                    if (nextSeasonNum <= numOfSeasons) {
                                        nextSeasonButtonShowOnEnd(nextSeasonNum, isTvShow)
                                    } else {
                                        nextSeasonButtonOnEndGone()
                                    }
                                })
                            }
                        }
                    } else {
                        nextSeasonButtonOnEndGone()
                    }
                } else {
                    nextSeasonButtonOnEndGone()
                }
            }
            override fun onFinish() {
            }
        }
    }

    private fun nextSeasonButtonShowOnEnd(nextSeasonNum: Int, isTvShow: Boolean) {
        when (playerView) {
            tv_title_player -> {
                tv_next_season_button.setVisible()
                tv_next_season_button.requestFocus()
                tv_next_season_button.text = "შემდეგი სეზონი: $nextSeasonNum"
                tv_next_season_button.setOnClickListener {
                    player.clearMediaItems()
                    videoPlayerViewModel.getPlaylistFiles(
                            videoPlayerViewModel.titleIdForDb.value!!,
                            nextSeasonNum,
                            videoPlayerViewModel.languageForDb.value!!
                    )
                    mediaPlayer.initPlayer(playerView, 0, 0L)

                }
            }
            phone_title_player -> {
                phone_next_season_button.setVisible()
                phone_next_season_button.requestFocus()
                phone_next_season_button.text = "შემდეგი სეზონი: $nextSeasonNum"
                phone_next_season_button.setOnClickListener {
                    player.clearMediaItems()
                    videoPlayerViewModel.getPlaylistFiles(
                            videoPlayerViewModel.titleIdForDb.value!!,
                            nextSeasonNum,
                            videoPlayerViewModel.languageForDb.value!!
                    )
                    mediaPlayer.initPlayer(playerView, 0, 0L)
                }
            }
        }
    }

    private fun nextSeasonButtonShow(nextSeasonNum: Int, isTvShow: Boolean) {
        when (playerView) {
            tv_title_player -> {
                tv_next_season_button_controller.setVisible()
                tv_next_season_button_controller.setOnClickListener {
                    player.clearMediaItems()
                    videoPlayerViewModel.getPlaylistFiles(
                            videoPlayerViewModel.titleIdForDb.value!!,
                            nextSeasonNum,
                            videoPlayerViewModel.languageForDb.value!!
                    )
                    mediaPlayer.initPlayer(playerView, 0, 0L)
                }
            }
            phone_title_player -> {
                phone_next_season_button_controller.setVisible()
                phone_next_season_button_controller.requestFocus()
                phone_next_season_button_controller.setOnClickListener {
                    player.clearMediaItems()
                    videoPlayerViewModel.getPlaylistFiles(
                            videoPlayerViewModel.titleIdForDb.value!!,
                            nextSeasonNum,
                            videoPlayerViewModel.languageForDb.value!!
                    )
                    mediaPlayer.initPlayer(playerView, 0, 0L)
                }
            }
        }
    }

    private fun nextSeasonButtonOnEndGone() {
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
        player.addTextOutput {
                subtitle?.onCues(it)
                tv_subtitle?.onCues(it)

            subtitle?.setStyle(CaptionStyleCompat(
                    ContextCompat.getColor(requireContext(), R.color.white),
                    ContextCompat.getColor(requireContext(), R.color.transparent),
                    ContextCompat.getColor(requireContext(), R.color.transparent),
                    EDGE_TYPE_DROP_SHADOW,
                    ContextCompat.getColor(requireContext(), R.color.black),
                    Typeface.DEFAULT_BOLD,
            ))
            subtitle?.setFixedTextSize(2, 25F)

            tv_subtitle?.setStyle(CaptionStyleCompat(
                    ContextCompat.getColor(requireContext(), R.color.white),
                    ContextCompat.getColor(requireContext(), R.color.transparent),
                    ContextCompat.getColor(requireContext(), R.color.transparent),
                    EDGE_TYPE_DROP_SHADOW,
                    ContextCompat.getColor(requireContext(), R.color.black),
                    Typeface.DEFAULT_BOLD,
            ))
            tv_subtitle?.setFixedTextSize(2, 25F)
        }

        if (hasSubs) {
            subtitle_toggle?.setVisible()
            tv_subtitle_toggle?.setVisible()
            subtitle_toggle?.setImageDrawable(resources.getDrawable(R.drawable.exo_subtitles_on, requireContext().theme))
            tv_subtitle_toggle?.setImageDrawable(resources.getDrawable(R.drawable.exo_subtitles_on, requireContext().theme))
        } else {
            subtitle_toggle?.setInvisible()
            tv_subtitle_toggle?.setInvisible()
        }

        subtitle_toggle?.setOnClickListener {
            if (subtitle.isVisible) {
                subtitle.setInvisible()
                subtitle_toggle.setImageDrawable(resources.getDrawable(R.drawable.exo_subtitles_off, requireContext().theme))
            } else {
                subtitle.setVisible()
                subtitle_toggle.setImageDrawable(resources.getDrawable(R.drawable.exo_subtitles_on, requireContext().theme))
            }
        }

        tv_subtitle_toggle?.setOnClickListener {
            if (tv_subtitle.isVisible) {
                tv_subtitle.setInvisible()
                tv_subtitle_toggle.setImageDrawable(resources.getDrawable(R.drawable.exo_subtitles_off, requireContext().theme))
            } else {
                tv_subtitle.setVisible()
                tv_subtitle_toggle.setImageDrawable(resources.getDrawable(R.drawable.exo_subtitles_on, requireContext().theme))
            }
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
            mediaPlayer.setMediaItems(listOf(MediaItem.fromUri(Uri.parse(trailerUrl))))
            mediaPlayer.initPlayer(playerView, chosenEpisode, watchedTime)
        } else {
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
        tracker.purgeHandler()
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