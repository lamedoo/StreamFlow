package com.lukakordzaia.streamflow.ui.phone.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvTrailerItemBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.squareup.picasso.Picasso

class TrailersAdapter(private val context: Context,
                      private val onTrailerClick: (trailerId: Int, trailerUrl: String?) -> Unit,
                      private val onTrailerInfoClick: (trailerId: Int) -> Unit) : RecyclerView.Adapter<TrailersAdapter.ViewHolder>() {
    private var list: List<SingleTitleModel> = ArrayList()

    fun setTrailerList(list: List<SingleTitleModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvTrailerItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailerModel = list[position]

        holder.bind(trailerModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvTrailerItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: SingleTitleModel) {
            view.rvTrailerName.text = model.displayName
            Glide.with(context)
                .load(model.cover?: R.drawable.movie_image_placeholder)
                .placeholder(R.drawable.movie_image_placeholder_landscape)
                .into(view.rvTrailerPoster)

            view.root.setOnClickListener {
                onTrailerClick(model.id, model.trailer)
            }

            view.rvTrailerInfo.setOnClickListener {
                onTrailerInfoClick(model.id)
            }
        }
    }
}