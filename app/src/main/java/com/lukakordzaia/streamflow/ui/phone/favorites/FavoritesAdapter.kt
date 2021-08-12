package com.lukakordzaia.streamflow.ui.phone.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvFavoriteItemBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.utils.setImage
import com.squareup.picasso.Picasso

class FavoritesAdapter(
    private val context: Context,
    private val onTitleClick: (titleId: Int) -> Unit,
    private val onMoreMenuClick: (titleId: Int) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {
    private var list: List<SingleTitleModel> = ArrayList()

    fun setItems(list: List<SingleTitleModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvFavoriteItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvFavoriteItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: SingleTitleModel) {
            view.itemPoster.setImage(model.poster, true)

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }

            view.removeItem.setOnClickListener {
                onMoreMenuClick(model.id)
            }
        }
    }
}