package com.lukakordzaia.streamflow.ui.phone.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_home_item.view.*

class HomeTvShowAdapter(private val context: Context, private val onTvShowClick: (id: Int) -> Unit) : RecyclerView.Adapter<HomeTvShowAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()

    fun setTvShowsList(list: List<TitleList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.rv_home_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.titleRoot.setOnClickListener {
            listModel.id?.let { tvShowId -> onTvShowClick(tvShowId) }
        }

        if (listModel.posters != null) {
            if (listModel.posters.data != null) {
                if (!listModel.posters.data.x240.isNullOrEmpty()) {
                    Picasso.get().load(listModel.posters.data.x240).into(holder.titlePosterImageView)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(holder.titlePosterImageView)
                }
            }
        }

//        if (!listModel.secondaryName.isNullOrEmpty()) {
//            holder.titleNameEngTextView.text = listModel.secondaryName
//        } else {
//            holder.titleNameEngTextView.text = ""
//        }

        if (!listModel.primaryName.isNullOrEmpty()) {
            holder.titleNameGeoTextView.text = listModel.primaryName
        } else {
            holder.titleNameGeoTextView.text = listModel.secondaryName
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleRoot: ConstraintLayout = view.rv_home_item_root
        val titlePosterImageView: ImageView = view.rv_home_item_poster
//        val titleNameEngTextView: TextView = view.rv_home_item_name_eng
        val titleNameGeoTextView: TextView = view.rv_home_item_name_geo
    }
}