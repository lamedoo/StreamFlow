package com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import kotlinx.android.synthetic.main.rv_choose_details_season_item.view.*

class ChooseTitleDetailsLanguageAdapter(
        private val context: Context,
        private val onLanguageClick: (language: String) -> Unit,
) : RecyclerView.Adapter<ChooseTitleDetailsLanguageAdapter.ViewHolder>() {
    private var list: List<String> = ArrayList()
    private var chosenLanguage: String = "ENG"

    fun setLanguageList(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setChosenLanguage(language: String) {
        chosenLanguage = language
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_choose_details_season_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val languageModel = list[position]

        val isChosen = languageModel == chosenLanguage

        if (isChosen) {
            holder.seasonContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rv_choose_details_season_chosen, null)
        } else {
            holder.seasonContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rv_choose_details_season, null)
        }

        holder.seasonContainer.setOnClickListener {
            onLanguageClick(languageModel)
        }

        holder.seasonNumberTextView.text = languageModel
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val seasonContainer: ConstraintLayout = view.rv_season_container
        val seasonNumberTextView: TextView = view.rv_season_number
    }
}