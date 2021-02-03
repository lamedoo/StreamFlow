package com.lukakordzaia.imoviesapp.ui.baseclasses

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.helpers.MediaPlayerClass
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.ui.phone.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.fragment_video_player.*
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseVideoPlayerFragment : Fragment(R.layout.fragment_video_player) {
    private val videoPlayerViewModel by viewModel<VideoPlayerViewModel>()

    private lateinit var mediaPlayer: MediaPlayerClass
    private lateinit var player: SimpleExoPlayer


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)


        videoPlayerViewModel.singleTitleData.observe(viewLifecycleOwner, {
            videoPlayerViewModel.getNumOfSeasons(it.seasons.data.size)
        })

        mediaPlayer.setPlayerListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
                    if (state == Player.STATE_READY) {
                        title_player.keepScreenOn = true
                        videoPlayerViewModel.isTvShow.observe(viewLifecycleOwner, {
                            if (it) {
                                val nextSeasonNum = videoPlayerViewModel.seasonForDb.value!! + 1
                                videoPlayerViewModel.numOfSeasons.observe(viewLifecycleOwner, { numOfSeasons ->
                                    if (nextSeasonNum <= numOfSeasons) {
                                        next_season_button.setVisible()
                                        next_season_button.text = "სეზონი: ${nextSeasonNum!!}"
                                        next_season_button.setOnClickListener { _ ->
                                            player.clearMediaItems()
                                            videoPlayerViewModel.getPlaylistFiles(
                                                videoPlayerViewModel.titleIdForDb.value!!,
                                                nextSeasonNum,
                                                videoPlayerViewModel.languageForDb.value!!
                                            )
                                            videoPlayerViewModel.initPlayer(it, 0L, 1)
                                        }
                                    } else {
                                        next_season_button.setGone()
                                    }
                                })
                            }
                        })
                    } else title_player.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)
                } else {
                    next_season_button.setGone()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                videoPlayerViewModel.setTitleName.observe(viewLifecycleOwner, { name ->
                    if (videoPlayerViewModel.isTvShow.value == true) {
                        header_tv.text = "ს${videoPlayerViewModel.seasonForDb.value}. ე${player.currentWindowIndex + 1}. ${name[player.currentWindowIndex]}"
                    } else {
                        header_tv.text = name[player.currentWindowIndex]
                    }
                })
            }
        })

        videoPlayerViewModel.episodesUri.observe(viewLifecycleOwner, {
            mediaPlayer.addAllEpisodes(it)
        })

        videoPlayerViewModel.playBackOptions.observe(viewLifecycleOwner, {
            mediaPlayer.initPlayer(title_player, it)
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

    fun getPlayListFiles(titleId: Int, chosenSeason: Int, chosenLanguage: String) {
        videoPlayerViewModel.getPlaylistFiles(titleId, chosenSeason, chosenLanguage)
        videoPlayerViewModel.getSingleTitleData(titleId)
    }

    fun initPlayer(isTvShow: Boolean, watchedTime: Long, chosenEpisode: Int, trailerUrl: String?) {
        if (trailerUrl != null) {
            mediaPlayer.setTrailerMediaItem(MediaItem.fromUri(Uri.parse(trailerUrl)))
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