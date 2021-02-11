package com.lukakordzaia.medootv.ui.phone.searchtitles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_search_item.view.*

class SearchTitlesAdapter(private val context: Context, private val onTitleClick: (id : Int) -> Unit) : RecyclerView.Adapter<SearchTitlesAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()

    fun setSearchTitleList(list: List<TitleList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_search_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.titleRoot.setOnClickListener {
            listModel.id?.let { titleId -> onTitleClick(titleId) }
        }

        if (listModel.posters != null) {
            if (listModel.posters.data != null) {
                if (!listModel.posters.data.x240.isNullOrEmpty()) {
                    Picasso.get().load(listModel.posters.data.x240).into(holder.titlePosterImageView)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(holder.titlePosterImageView)
                }
            }
        }

        if (!listModel.secondaryName.isNullOrEmpty()) {
            holder.titleNameGeoTextView.text = listModel.secondaryName
        } else {
            holder.titleNameGeoTextView.text = listModel.primaryName
        }

        holder.titleYearTextView.text = "${listModel.year.toString()}, "

        if (listModel.isTvShow == true) {
            holder.titleIsTvShowTextView.text = "სერიალი"
        } else {
            holder.titleIsTvShowTextView.text = "ფილმი"
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleRoot: ConstraintLayout = view.rv_search_item_root
        val titlePosterImageView: ImageView = view.rv_search_item_poster
        val titleNameGeoTextView: TextView = view.rv_search_item_name_geo
        val titleYearTextView: TextView = view.rv_search_item_year
        val titleIsTvShowTextView: TextView = view.rv_search_item_istvshow
    }
}