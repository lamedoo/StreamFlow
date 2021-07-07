package com.lukakordzaia.streamflow.ui.phone.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvGenreItemBinding
import com.lukakordzaia.streamflow.datamodels.GenreList
import kotlinx.android.synthetic.main.rv_genre_item.view.*

class GenresAdapter(
    private val context: Context,
    private val onGenreClick: (genreId: Int, genreName: String) -> Unit
) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {
    private var list: List<GenreList.Data> = ArrayList()

    fun setGenreList(list: List<GenreList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvGenreItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genreModel = list[position]

        holder.bind(genreModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvGenreItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: GenreList.Data) {
            view.rvGenreName.apply {
                text = model.primaryName
                setOnClickListener {
                    onGenreClick(model.id, model.primaryName)
                }
            }
        }
    }
}