package com.lukakordzaia.streamflow.ui.phone.catalogue.catalogueadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.streamflow.databinding.RvGenreItemBinding

class GenresAdapter(
    private val context: Context,
    private val onGenreClick: (genreId: Int, genreName: String) -> Unit
) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {
    private var list: List<GetGenresResponse.Data> = ArrayList()

    fun setGenreList(list: List<GetGenresResponse.Data>) {
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
        fun bind(model: GetGenresResponse.Data) {
            view.rvGenreName.apply {
                text = model.primaryName
                setOnClickListener {
                    onGenreClick(model.id, model.primaryName)
                }
            }
        }
    }
}