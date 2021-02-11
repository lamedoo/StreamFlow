package com.lukakordzaia.streamflow.ui.phone.categories

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
import kotlinx.android.synthetic.main.phone_single_title_fragment.*
import kotlinx.android.synthetic.main.rv_trailer_item.view.*

class TrailersAdapter(private val context: Context, private val onTrailerClick: (trailerId: Int, trailerUrl: String) -> Unit) : RecyclerView.Adapter<TrailersAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()

    fun setTrailerList(list: List<TitleList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_trailer_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailerModel = list[position]

        if (trailerModel.covers != null) {
            if (trailerModel.covers.data != null) {
                if (!trailerModel.covers.data.x1050.isNullOrEmpty()) {
                    Picasso.get().load(trailerModel.covers.data.x1050).into(holder.trailerPosterImageView)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(holder.trailerPosterImageView)
                }
            }
        }

        if (!trailerModel.primaryName.isNullOrEmpty()) {
            holder.trailerNameTextView.text = trailerModel.primaryName
        } else {
            holder.trailerNameTextView.text = trailerModel.secondaryName
        }

        holder.trailerRoot.setOnClickListener {
            onTrailerClick(trailerModel.id, trailerModel.trailers!!.data!![0]!!.fileUrl)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trailerRoot: ConstraintLayout = view.rv_trailer_root
        val trailerPosterImageView: ImageView = view.rv_trailer_poster
        val trailerNameTextView: TextView = view.rv_trailer_name
    }
}