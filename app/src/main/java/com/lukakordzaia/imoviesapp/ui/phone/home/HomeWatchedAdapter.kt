package com.lukakordzaia.imoviesapp.ui.phone.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.datamodels.WatchedTitleData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_watched_title_item.view.*
import java.util.concurrent.TimeUnit

class HomeWatchedAdapter(
    private val context: Context,
    private val onWatchedTitleClick: (watchedTitleData: WatchedTitleData) -> Unit,
    private val onMoreMenuClick: (titleId: Int, view: View) -> Unit
) : RecyclerView.Adapter<HomeWatchedAdapter.ViewHolder>() {
    private var list: List<WatchedTitleData> = ArrayList()

    fun setWatchedTitlesList(list: List<WatchedTitleData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_watched_title_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.titleWatchedRoot.setOnClickListener {
            onWatchedTitleClick(listModel)
        }

        if (listModel.isTvShow) {
            holder.titleWatchedSeason.text = "ს${listModel.season} ე${listModel.episode} / ${String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(listModel.watchedTime),
                    TimeUnit.MILLISECONDS.toSeconds(listModel.watchedTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(listModel.watchedTime))
            )}"
        } else {
            holder.titleWatchedSeason.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(listModel.watchedTime),
                    TimeUnit.MILLISECONDS.toSeconds(listModel.watchedTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(listModel.watchedTime))
            )
        }

        if (listModel.cover != null) {
            Picasso.get().load(listModel.cover).into(holder.titleWatchedPosterImageView)
        } else {
            Picasso.get().load(R.drawable.movie_image_placeholder)
                .into(holder.titleWatchedPosterImageView)
        }

        holder.titleWatchedMore.setOnClickListener {
            onMoreMenuClick(listModel.id, holder.titleWatchedMore)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleWatchedRoot: ConstraintLayout = view.rv_watchedtitle_item_root
        val titleWatchedPosterImageView: ImageView = view.rv_watchedtitle_item_poster
        val titleWatchedSeason: TextView = view.rv_watchedtitle_item_season
        val titleWatchedMore: ImageView = view.rv_watchedtitle_item_more
    }
}