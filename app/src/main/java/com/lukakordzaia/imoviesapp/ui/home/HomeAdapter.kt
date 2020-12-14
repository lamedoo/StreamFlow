package com.lukakordzaia.imoviesapp.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.models.MoviesList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_movie_item.view.*

class HomeAdapter(private val context: Context, private val onMovieClick: (id: Int) -> Unit) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private var list: List<MoviesList.Data> = ArrayList()

    fun setMoviesList(list: List<MoviesList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.rv_movie_item, parent, false)
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
        }

        if (!listModel.primaryName.isNullOrEmpty()) {
            holder.movieTiTleGeoTextView.text = listModel.primaryName
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieRoot: ConstraintLayout = view.rv_movie_root
        val moviePosterImageView: ImageView = view.rv_movie_poster
        val movieTitleEngTextView: TextView = view.rv_movie_title_eng
        val movieTiTleGeoTextView: TextView = view.rv_movie_title_geo
    }

}