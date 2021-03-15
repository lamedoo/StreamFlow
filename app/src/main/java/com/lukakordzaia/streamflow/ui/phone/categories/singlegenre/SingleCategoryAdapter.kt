package com.lukakordzaia.streamflow.ui.phone.categories.singlegenre

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_single_genre_item.view.*

class SingleCategoryAdapter(private val context: Context, private val onTitleClick: (id : Int) -> Unit) : RecyclerView.Adapter<SingleCategoryAdapter.ViewHolder>() {
    private var list: List<TitleList.Data> = ArrayList()
    var startPosition = 0

    fun setCategoryTitleList(list: List<TitleList.Data>) {
        Log.d("newmovieslist", startPosition.toString())
        this.list += list
        notifyItemRangeChanged(startPosition, list.size)
        startPosition += list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.rv_single_genre_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.titleRoot.setOnClickListener {
            onTitleClick(listModel.id)
            it.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        }


                    Picasso.get().load(listModel.posters?.data?.x240).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(holder.titlePosterImageView)

        if (listModel.primaryName.isNotEmpty()) {
            holder.titleNameGeoTextView.text = listModel.primaryName
        } else {
            holder.titleNameGeoTextView.text = listModel.secondaryName
        }

        if (listModel.isTvShow == true) {
            holder.isTvShowTextView.text = "სერიალი"
        } else {
            holder.isTvShowTextView.text = "ფილმი"
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleRoot: ConstraintLayout = view.rv_singlegenre_item_root
        val titlePosterImageView: ImageView = view.rv_singlegenre_item_poster
        val titleNameGeoTextView: TextView = view.rv_singlegenre_item_name_geo
        val isTvShowTextView: TextView = view.rv_single_genre_istvshow
    }
}