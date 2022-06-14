package com.lukakordzaia.streamflowphone.ui.catalogue.catalogueadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.streamflowphone.databinding.RvGenreItemBinding

class GenresAdapter(
    private val onGenreClick: (genreId: Int, genreName: String) -> Unit
) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {
    private var list: List<GetGenresResponse.Data> = ArrayList()

    fun setGenreList(list: List<GetGenresResponse.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvGenreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genreModel = list[position]

        holder.bind(genreModel, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvGenreItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: GetGenresResponse.Data, position: Int) {
//            view.container.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(getRandomColor(position))))
            view.rvGenreName.apply {
                text = model.primaryName
                setOnClickListener {
                    onGenreClick(model.id, model.primaryName)
                }
            }
        }

        private fun getRandomColor(position: Int): String {
            val colors = arrayOf(
                "#0b2e4b",
                "#6aa3ca",
                "#dbc796",
                "#544f2b",
                "#a02f25",
                "#0b2e4b",
                "#6aa3ca",
                "#dbc796",
                "#544f2b",
                "#a02f25",
                "#0b2e4b",
                "#6aa3ca",
                "#dbc796",
                "#544f2b",
                "#a02f25",
                "#0b2e4b",
                "#6aa3ca",
                "#dbc796",
                "#544f2b",
                "#a02f25",
                "#0b2e4b",
                "#6aa3ca",
                "#dbc796",
                "#544f2b",
                "#a02f25",
                "#0b2e4b",
                "#6aa3ca",
                "#dbc796",
                "#544f2b",
                "#a02f25"
            )
            return colors[position]
        }
    }
}