package com.lukakordzaia.imoviesapp.ui.baseclasses

import android.content.pm.ActivityInfo
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
import com.lukakordzaia.imoviesapp.ui.phone.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.fragment_video_player.*

open class BaseVideoPlayerFragment : Fragment(R.layout.fragment_video_player) {
    private lateinit var viewModel: VideoPlayerViewModel
    private lateinit var mediaPlayer: MediaPlayerClass
    private lateinit var player: SimpleExoPlayer


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        viewModel = ViewModelProvider(this).get(VideoPlayerViewModel::class.java)
        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaPlayer = MediaPlayerClass(player)

        mediaPlayer.setPlayerListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if ((player.currentWindowIndex + 1) == player.mediaItemCount) {
                    if (state == Player.STATE_READY) {
                        title_player.keepScreenOn = true
                        viewModel.isTvShow.observe(viewLifecycleOwner, {
                            if (it) {
                                val nextSeasonNum = viewModel.seasonForDb.value
                                next_season_button.setVisible()
                                next_season_button.text = "სეზონი: ${nextSeasonNum!! + 1}"
                                next_season_button.setOnClickListener { _ ->
                                    player.clearMediaItems()
                                    viewModel.getPlaylistFiles(viewModel.titleIdForDb.value!!, nextSeasonNum + 1, viewModel.languageForDb.value!!)
                                    viewModel.initPlayer(it, 0L, 1)
                                }
                            }
                        })
                    } else title_player.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)
                } else {
                    next_season_button.setGone()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                viewModel.setTitleName.observe(viewLifecycleOwner, { name ->
                    header_tv.text = name[player.currentWindowIndex]
                })
            }
        })

        viewModel.episodesUri.observe(viewLifecycleOwner, {
            mediaPlayer.addAllEpisodes(it)
        })

        viewModel.playBackOptions.observe(viewLifecycleOwner, {
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
        viewModel.getPlaylistFiles(titleId, chosenSeason, chosenLanguage)
    }

    fun initPlayer(isTvShow: Boolean, watchedTime: Long, chosenEpisode: Int, trailerUrl: String?) {
        if (trailerUrl != null) {
            mediaPlayer.setTrailerMediaItem(MediaItem.fromUri(Uri.parse(trailerUrl)))
        }
        viewModel.initPlayer(isTvShow, watchedTime, chosenEpisode)
    }

    fun releasePlayer(titleId: Int, isTvShow: Boolean, chosenLanguage: String, trailerUrl: String?) {
        mediaPlayer.releasePlayer {
            viewModel.releasePlayer(it)
        }
        if (trailerUrl == null) {
            viewModel.saveTitleToDb(requireContext(), titleId, isTvShow, chosenLanguage)
        }
    }
}