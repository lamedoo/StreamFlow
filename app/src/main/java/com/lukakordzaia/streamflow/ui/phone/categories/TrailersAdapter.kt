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
import com.lukakordzaia.streamflow.databinding.RvTrailerItemBinding
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_trailer_item.view.*

class TrailersAdapter(private val context: Context,
                      private val onTrailerClick: (trailerId: Int, trailerUrl: String) -> Unit,
                      private val onTrailerInfoClick: (trailerId: Int) -> Unit) : RecyclerView.Adapter<TrailersAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()

    fun setTrailerList(list: List<TitleList.Data>) {
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
        fun bind(model: TitleList.Data) {
            if (model.primaryName.isNotEmpty()) {
                view.rvTrailerName.text = model.primaryName
            } else {
                view.rvTrailerName.text = model.secondaryName
            }

            Picasso.get().load(model.covers?.data?.x1050).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(view.rvTrailerPoster)

            view.root.setOnClickListener {
                onTrailerClick(model.id, model.trailers!!.data!![0]!!.fileUrl)
            }

            view.rvTrailerInfo.setOnClickListener {
                onTrailerInfoClick(model.id)
            }
        }
    }
}