package com.lukakordzaia.core.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.databinding.RvChooseLanguageItemBinding

class ChooseLanguageAdapter(
    private val onLanguageClick: (language: String) -> Unit)
    : RecyclerView.Adapter<ChooseLanguageAdapter.ViewHolder>() {
    private var list: List<String> = ArrayList()

    fun setLanguageList(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvChooseLanguageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvChooseLanguageItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: String) {
            view.rvLanguageName.text = model
            view.rvLanguageName.setOnClickListener {
                onLanguageClick(model)
            }
        }
    }

}