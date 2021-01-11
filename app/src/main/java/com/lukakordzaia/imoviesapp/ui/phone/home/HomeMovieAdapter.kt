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
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_title_item.view.*

class HomeMovieAdapter(private val context: Context, private val onMovieClick: (id: Int) -> Unit) : RecyclerView.Adapter<HomeMovieAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()

    fun setMoviesList(list: List<TitleList.Data>) {
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

        holder.movieRoot.setOnClickListener {
            listModel.id?.let { movieId -> onMovieClick(movieId) }
        }

        if (listModel.covers != null) {
            if (listModel.covers.data != null) {
                if (!listModel.covers.data.x510.isNullOrEmpty()) {
                    Picasso.get().load(listModel.covers.data.x510).into(holder.moviePosterImageView)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(holder.moviePosterImageView)
                }
            }
        }

        if (!listModel.secondaryName.isNullOrEmpty()) {
            holder.movieTitleEngTextView.text = listModel.secondaryName
        } else {
            holder.movieTitleEngTextView.text = ""
        }

        if (!listModel.primaryName.isNullOrEmpty()) {
            holder.movieTitleGeoTextView.text = listModel.primaryName
        } else {
            holder.movieTitleGeoTextView.text = ""
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieRoot: ConstraintLayout = view.rv_title_root
        val moviePosterImageView: ImageView = view.rv_title_poster
        val movieTitleEngTextView: TextView = view.rv_title_name_eng
        val movieTitleGeoTextView: TextView = view.rv_title_name_geo
    }

}