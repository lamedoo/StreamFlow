package com.lukakordzaia.streamflow.ui.phone.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_db_title_item.view.*
import java.util.concurrent.TimeUnit

class HomeDbTitlesAdapter(
    private val context: Context,
    private val onWatchedTitleClick: (dbTitleData: DbTitleData) -> Unit,
    private val onInfoClick: (titleId: Int) -> Unit,
    private val onMoreMenuClick: (titleId: Int, view: View) -> Unit
) : RecyclerView.Adapter<HomeDbTitlesAdapter.ViewHolder>() {
    private var list: List<DbTitleData> = ArrayList()

    fun setWatchedTitlesList(list: List<DbTitleData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_db_title_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.dbTitleRoot.setOnClickListener {
            onWatchedTitleClick(listModel)
        }

        if (listModel.isTvShow) {
            holder.dbTitleSeason.text = String.format("ს${listModel.season} ე${listModel.episode} / %02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(listModel.watchedDuration),
                    TimeUnit.MILLISECONDS.toSeconds(listModel.watchedDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(listModel.watchedDuration))
            )
        } else {
            holder.dbTitleSeason.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(listModel.watchedDuration),
                    TimeUnit.MILLISECONDS.toSeconds(listModel.watchedDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(listModel.watchedDuration))
            )
        }

        if (listModel.cover != null) {
            Picasso.get().load(listModel.cover).into(holder.dbTitlePosterImageView)
        } else {
            Picasso.get().load(R.drawable.movie_image_placeholder)
                .into(holder.dbTitlePosterImageView)
        }

        holder.dbTitleInfo.setOnClickListener {
            onInfoClick(listModel.id)
        }

        holder.dbTitleMore.setOnClickListener {
            onMoreMenuClick(listModel.id, holder.dbTitleMore)
        }

        holder.dbTitleSeekBar.max = listModel.titleDuration.toInt()
        holder.dbTitleSeekBar.progress = listModel.watchedDuration.toInt()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dbTitleRoot: ConstraintLayout = view.rv_watchedtitle_item_root
        val dbTitlePosterImageView: ImageView = view.rv_watchedtitle_item_poster
        val dbTitleSeason: TextView = view.rv_watchedtitle_item_season
        val dbTitleMore: ImageView = view.rv_watchedtitle_item_more
        val dbTitleInfo: ImageView = view.rv_watchedtitle_item_info
        val dbTitleSeekBar: AppCompatSeekBar = view.rv_db_item_seekbar
    }
}