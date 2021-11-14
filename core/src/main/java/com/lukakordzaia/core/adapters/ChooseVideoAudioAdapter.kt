package com.lukakordzaia.core.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.R
import com.lukakordzaia.core.databinding.RvChooseAudioItemBinding
import com.lukakordzaia.core.utils.Enums
import com.lukakordzaia.core.utils.setVisibleOrGone

class ChooseVideoAudioAdapter(
    private val context: Context,
    private val onLanguageClick: (languages: String) -> Unit) : RecyclerView.Adapter<ChooseVideoAudioAdapter.ViewHolder>() {
    private var list: List<String> = ArrayList()
    private var currentItem: String = ""

    fun setItems(list: List<String>) {
        this.list = list
        notifyItemRangeInserted(0, list.size)
    }

    fun setCurrentItem(current: String) {
        currentItem = current
        notifyDataSetChanged()
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
            view.languageName.text = transformLanguages(model)

            view.currentIndicator.setVisibleOrGone(currentItem.equals(model, true))

            view.root.setOnClickListener {
                onLanguageClick(model)
            }
        }

        private fun transformLanguages(language: String): String {
            return when (true) {
                language.equals(Enums.Languages.ENG.toString(), true) -> context.resources.getString(R.string.english)
                language.equals(Enums.Languages.RUS.toString(), true) -> context.resources.getString(R.string.russian)
                language.equals(Enums.Languages.GEO.toString(), true)-> context.resources.getString(R.string.georgian)
                else -> language.uppercase()
            }
        }
    }

}