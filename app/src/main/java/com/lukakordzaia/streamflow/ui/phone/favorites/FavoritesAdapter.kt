package com.lukakordzaia.streamflow.ui.phone.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvFavoriteItemBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_favorite_item.view.*

class FavoritesAdapter(
    private val context: Context,
    private val onTitleClick: (titleId: Int) -> Unit,
    private val onMoreMenuClick: (titleId: Int) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {
    private var list: List<SingleTitleData.Data> = ArrayList()

    fun setItems(list: List<SingleTitleData.Data>) {
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
        fun bind(model: SingleTitleData.Data) {
            Picasso.get().load(model.posters.data?.x240).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(view.itemPoster)

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }

            view.removeItem.setOnClickListener {
                onMoreMenuClick(model.id)
            }
        }
    }
}