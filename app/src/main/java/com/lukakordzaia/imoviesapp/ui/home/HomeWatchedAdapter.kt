package com.lukakordzaia.imoviesapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.WatchedTitleData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_title_item.view.*

class HomeWatchedAdapter(
    private val context: Context,
    private val onWatchedTitleClick: (watchedTitleData: WatchedTitleData) -> Unit
) : RecyclerView.Adapter<HomeWatchedAdapter.ViewHolder>() {
    private var list: List<WatchedTitleData> = ArrayList()

    fun setWatchedTitlesList(list: List<WatchedTitleData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_title_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.titleRoot.setOnClickListener {
            onWatchedTitleClick(listModel)
        }

        if (listModel.cover != null) {
            Picasso.get().load(listModel.cover).into(holder.titlePosterImageView)
        } else {
            Picasso.get().load(R.drawable.movie_image_placeholder)
                .into(holder.titlePosterImageView)
        }

        if (!listModel.originalName.isNullOrEmpty()) {
            holder.titleNameEngTextView.text = listModel.primaryName
        } else {
            holder.titleNameEngTextView.text = ""
        }

        if (!listModel.primaryName.isNullOrEmpty()) {
            holder.titleNameGeoTextView.text = listModel.primaryName
        } else {
            holder.titleNameGeoTextView.text = ""
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleRoot: ConstraintLayout = view.rv_title_root
        val titlePosterImageView: ImageView = view.rv_title_poster
        val titleNameEngTextView: TextView = view.rv_title_name_eng
        val titleNameGeoTextView: TextView = view.rv_title_name_geo
    }
}