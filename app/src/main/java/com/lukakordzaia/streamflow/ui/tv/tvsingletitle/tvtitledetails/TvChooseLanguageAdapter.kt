package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import kotlinx.android.synthetic.main.rv_tv_choose_language_item.view.*

class TvChooseLanguageAdapter(private val context: Context, private val onLanguageClick: (language: String) -> Unit) : RecyclerView.Adapter<TvChooseLanguageAdapter.ViewHolder>() {
    private var list: List<String> = ArrayList()

    fun setLanguageList(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.rv_tv_choose_language_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.languageTextView.text = listModel

        holder.languageTextView.setOnClickListener {
            onLanguageClick(listModel)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val languageTextView: TextView = view.rv_language_name
    }

}