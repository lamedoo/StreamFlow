package com.lukakordzaia.medootv.ui.phone.genres

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.datamodels.GenreList
import kotlinx.android.synthetic.main.rv_genre_item.view.*

class GenresAdapter(private val context: Context, private val onGenreClick: (genreId: Int) -> Unit) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {
    private var list: List<GenreList.Data> = ArrayList()

    fun setGenreList(list: List<GenreList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_genre_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genreModel = list[position]

        holder.genreNameTextView.text = genreModel.primaryName
        holder.genreNameTextView.setOnClickListener {
            onGenreClick(genreModel.id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val genreNameTextView: TextView = view.rv_genre_name
    }
}