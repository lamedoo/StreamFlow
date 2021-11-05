package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.databinding.RvChooseAudioItemBinding

class TvChooseAudioAdapter(
    private val context: Context,
    private val onLanguageClick: (languages: String) -> Unit) : RecyclerView.Adapter<TvChooseAudioAdapter.ViewHolder>() {
    private var list: List<String> = ArrayList()

    fun setItems(list: List<String>) {
        this.list = list
        notifyItemRangeInserted(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvChooseAudioItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvChooseAudioItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: String) {
            view.languageName.text = model

            view.root.setOnClickListener {
                onLanguageClick(model)
            }
        }
    }

}