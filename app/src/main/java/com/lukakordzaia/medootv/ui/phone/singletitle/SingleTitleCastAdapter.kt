package com.lukakordzaia.medootv.ui.phone.singletitle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.datamodels.TitleCast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_single_title_cast_item.view.*

class SingleTitleCastAdapter(private val context: Context, private val onCastClick: (name: String) -> Unit) : RecyclerView.Adapter<SingleTitleCastAdapter.ViewHolder>() {
    private var list: List<TitleCast.Data> = ArrayList()

    fun setCastList(list: List<TitleCast.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.rv_single_title_cast_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.castRoot.setOnClickListener {
            onCastClick(listModel.originalName)
        }

        if (listModel.poster.isNotEmpty()) {
            Picasso.get().load(listModel.poster).into(holder.castPosterImageView)
        } else {
            Picasso.get().load(R.drawable.movie_image_placeholder).into(holder.castPosterImageView)
        }

        if (listModel.primaryName.isNotEmpty()) {
            holder.castNameTextView.text = listModel.primaryName
        } else {
            holder.castNameTextView.text = listModel.originalName
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val castRoot: ConstraintLayout = view.rv_cast_item_root
        val castPosterImageView: ImageView = view.rv_cast_item_poster
        val castNameTextView: TextView = view.rv_cast_item_name
    }

}