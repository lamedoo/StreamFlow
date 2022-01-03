package com.lukakordzaia.streamflowphone.ui.catalogue.catalogueadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.streamflowphone.databinding.RvTrailerItemBinding

class TrailersAdapter(
    private val onTrailerClick: (trailerId: Int, trailerUrl: String?) -> Unit,
    private val onTrailerInfoClick: (trailerId: Int) -> Unit
) : RecyclerView.Adapter<TrailersAdapter.ViewHolder>() {
    private var list: List<SingleTitleModel> = ArrayList()

    fun setTrailerList(list: List<SingleTitleModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvTrailerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            view.rvTrailerPoster.setImage(model.poster, true)

            view.root.setOnClickListener {
                onTrailerClick(model.id, model.trailer)
            }

            view.rvTrailerInfo.setOnClickListener {
                onTrailerInfoClick(model.id)
            }
        }
    }
}