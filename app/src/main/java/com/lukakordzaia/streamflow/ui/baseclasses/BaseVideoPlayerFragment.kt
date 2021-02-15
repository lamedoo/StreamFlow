package com.lukakordzaia.streamflow.ui.baseclasses

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.helpers.videoplayer.MediaPlayerClass
import com.lukakordzaia.streamflow.helpers.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.phone_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.phone_fragment_video_player.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.tv_video_player_fragment.*
import org.koin.android.ext.android.inject

abstract class BaseVideoPlayerFragment(fragment: Int) : Fragment(fragment) {
    abstract val videoPlayerViewModel: VideoPlayerViewModel
    private val buildMediaSource: BuildMediaSource by inject()

    private lateinit var mediaPlayer: MediaPlayerClass
    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)

//        player.addTextOutput {
//            subtitle?.onCues(it)
//        }

        mediaPlayer.setPlayerListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
                    if (state == Player.STATE_READY) {
                        playerView.keepScreenOn = true
                        videoPlayerViewModel.isTvShow.observe(viewLifecycleOwner, {
                            if (it) {
                                val nextSeasonNum = videoPlayerViewModel.seasonForDb.value!! + 1
                                videoPlayerViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                                    if (nextSeasonNum <= numOfSeasons) {
                                        nextSeasonButtonShow(nextSeasonNum, it)
                                    } else {
                                        nextSeasonButtonGone()
                                    }
                                })
                            }
                        })
                    } else playerView.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)
                } else {
                    nextSeasonButtonGone()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                videoPlayerViewModel.setTitleName.observe(viewLifecycleOwner, { name ->
                    if (videoPlayerViewModel.isTvShow.value == true) {
                        setEpisodeName(name)
                    } else {
                        if (name.isNullOrEmpty()) {
                            setMovieName("ტრეილერი")
                        } else {
                            setMovieName(name[player.currentWindowIndex])
                        }
                    }
                })
            }
        })

        videoPlayerViewModel.mediaAndSubtitle.observe(viewLifecycleOwner, {
            if (it.first.size == 1) {
                mediaPlayer.setPlayerMediaSource(buildMediaSource.movieMediaSource(it.first, it.second))
            } else if (it.first.size > 1) {
                mediaPlayer.setMultipleMediaSources(buildMediaSource.tvShowMediaSource(it.first, it.second))
            }
        })

        videoPlayerViewModel.playBackOptions.observe(viewLifecycleOwner, {
            mediaPlayer.initPlayer(playerView, it)
        })

        videoPlayerViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it, Toast.LENGTH_LONG)
        })
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
        } else {
            requireActivity().window.decorView.systemUiVisibility = View.VISIBLE
        }
    }

    private fun nextSeasonButtonShow(nextSeasonNum: Int, isTvShow: Boolean) {
        when (playerView) {
            phone_title_player -> {
                next_season_button.setVisible()
                next_season_button.text = "სეზონი: ${nextSeasonNum!!}"
                next_season_button.setOnClickListener { _ ->
                    player.clearMediaItems()
                    videoPlayerViewModel.getPlaylistFiles(
                            videoPlayerViewModel.titleIdForDb.value!!,
                            nextSeasonNum,
                            videoPlayerViewModel.languageForDb.value!!
                    )
                    videoPlayerViewModel.initPlayer(isTvShow, 0L, 1)
                }
            }
            tv_title_player -> {
                tv_next_season_button.setVisible()
                tv_next_season_button.text = "სეზონი: ${nextSeasonNum!!}"
                tv_next_season_button.setOnClickListener { _ ->
                    player.clearMediaItems()
                    videoPlayerViewModel.getPlaylistFiles(
                            videoPlayerViewModel.titleIdForDb.value!!,
                            nextSeasonNum,
                            videoPlayerViewModel.languageForDb.value!!
                    )
                    videoPlayerViewModel.initPlayer(isTvShow, 0L, 1)
                }
            }
        }
    }

    private fun nextSeasonButtonGone() {
        when (playerView) {
            phone_title_player -> {
                next_season_button.setGone()
            }
            tv_title_player -> {
                tv_next_season_button.setGone()
            }
        }
    }

    private fun setEpisodeName(names: List<String>) {
        when (playerView) {
            phone_title_player -> {
                header_tv.text = "ს${videoPlayerViewModel.seasonForDb.value}. ე${player.currentWindowIndex + 1}. ${names[player.currentWindowIndex]}"
            }
            tv_title_player -> {
                tv_header_tv.text = "ს${videoPlayerViewModel.seasonForDb.value}. ე${player.currentWindowIndex + 1}. ${names[player.currentWindowIndex]}"
            }
        }
    }

    private fun setMovieName(name: String) {
        when (playerView) {
            phone_title_player -> {
                header_tv.text = name
            }
            tv_title_player -> {
                tv_header_tv.text = name
            }
        }
    }

    fun setExoPlayer(playerView: PlayerView) {
        this.playerView = playerView
    }

    fun getPlayListFiles(titleId: Int, chosenSeason: Int, chosenLanguage: String) {
        videoPlayerViewModel.getPlaylistFiles(titleId, chosenSeason, chosenLanguage)
        videoPlayerViewModel.getSingleTitleData(titleId)
    }

    fun initPlayer(isTvShow: Boolean, watchedTime: Long, chosenEpisode: Int, trailerUrl: String?) {
        if (trailerUrl != null) {
            mediaPlayer.setMediaItems(listOf(MediaItem.fromUri(Uri.parse(trailerUrl))))
        }
        videoPlayerViewModel.initPlayer(isTvShow, watchedTime, chosenEpisode)
    }

    fun releasePlayer(titleId: Int, isTvShow: Boolean, chosenLanguage: String, trailerUrl: String?) {
        mediaPlayer.releasePlayer {
            videoPlayerViewModel.releasePlayer(it)
        }
        if (trailerUrl == null) {
            videoPlayerViewModel.saveTitleToDb(requireContext(), titleId, isTvShow, chosenLanguage)
        }
    }
}