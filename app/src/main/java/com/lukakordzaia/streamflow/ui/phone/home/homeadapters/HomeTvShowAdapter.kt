package com.lukakordzaia.streamflow.ui.phone.home.homeadapters

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
import kotlinx.android.synthetic.main.rv_home_item.view.*

class HomeTvShowAdapter(private val context: Context, private val onTvShowClick: (id: Int) -> Unit) : RecyclerView.Adapter<HomeTvShowAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()

    fun setTvShowsList(list: List<TitleList.Data>) {
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

        holder.titleRoot.setOnClickListener {
            onTvShowClick(listModel.id)
        }

        Picasso.get().load(listModel.posters?.data?.x240).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(holder.titlePosterImageView)

        if (listModel.primaryName.isNotEmpty()) {
            holder.titleNameGeoTextView.text = listModel.primaryName
        } else {
            holder.titleNameGeoTextView.text = listModel.secondaryName
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleRoot: ConstraintLayout = view.rv_home_item_root
        val titlePosterImageView: ImageView = view.rv_home_item_poster
        val titleNameGeoTextView: TextView = view.rv_home_item_name_geo
    }
}