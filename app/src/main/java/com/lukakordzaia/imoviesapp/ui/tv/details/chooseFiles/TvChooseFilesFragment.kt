package com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.SpinnerClass
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.tv_choose_files_fragment.*

class TvChooseFilesFragment : Fragment(R.layout.tv_choose_files_fragment) {
    private lateinit var viewModel: ChooseTitleDetailsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean
        viewModel = ViewModelProvider(this).get(ChooseTitleDetailsViewModel::class.java)
        viewModel.getSingleTitleFiles(titleId)
        val spinnerClass = SpinnerClass(requireContext())

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                tv_file_not_yet.setGone()
                tv_files_container.setVisible()
            }
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(tv_files_spinner_language, languages) { language ->
                viewModel.getTitleLanguageFiles(language)
            }
        })

        tv_play.setOnClickListener {
            val intent = Intent(context, TvVideoPlayerActivity::class.java)
            intent.putExtra("titleId", titleId)
            intent.putExtra("isTvShow", isTvShow)
            intent.putExtra("chosenLanguage", viewModel.chosenLanguage.value)
            intent.putExtra("chosenSeason", 0)
            intent.putExtra("chosenEpisode", 0)
            intent.putExtra("watchedTime", 0L)
            activity?.startActivity(intent)
        }
    }
}