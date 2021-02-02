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
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_home_item.view.*

class HomeNewMovieAdapter(private val context: Context, private val onMovieClick: (id: Int) -> Unit) : RecyclerView.Adapter<HomeNewMovieAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()

    fun setMoviesList(list: List<TitleList.Data>) {
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

        holder.movieRoot.setOnClickListener {
            listModel.id?.let { movieId -> onMovieClick(movieId) }
        }

        if (listModel.posters != null) {
            if (listModel.posters.data != null) {
                if (!listModel.posters.data.x240.isNullOrEmpty()) {
                    Picasso.get().load(listModel.posters.data.x240).into(holder.moviePosterImageView)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(holder.moviePosterImageView)
                }
            }
        }

        if (!listModel.primaryName.isNullOrEmpty()) {
            holder.movieTitleGeoTextView.text = listModel.primaryName
        } else {
            holder.movieTitleGeoTextView.text = listModel.secondaryName
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieRoot: ConstraintLayout = view.rv_home_item_root
        val moviePosterImageView: ImageView = view.rv_home_item_poster
        val movieTitleGeoTextView: TextView = view.rv_home_item_name_geo
    }

}