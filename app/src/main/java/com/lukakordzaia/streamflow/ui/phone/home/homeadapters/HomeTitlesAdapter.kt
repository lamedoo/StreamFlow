package com.lukakordzaia.streamflow.ui.phone.home.homeadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvHomeItemBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.utils.setImage
import com.squareup.picasso.Picasso

class HomeTitlesAdapter(private val context: Context, private val onTitleClick: (id: Int) -> Unit) : RecyclerView.Adapter<HomeTitlesAdapter.ViewHolder>() {
    private var list: List<SingleTitleModel> = ArrayList()

    fun setItems(list: List<SingleTitleModel>) {
        this.list = list
        notifyItemRangeInserted(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvHomeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvHomeItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: SingleTitleModel) {
            view.itemName.text = model.displayName
            view.itemPoster.setImage(model.poster, true)

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }
        }
    }

}