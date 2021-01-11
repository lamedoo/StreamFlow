package com.lukakordzaia.imoviesapp.ui.tv.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.TitleData
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles.TvChooseFilesActivity
import com.lukakordzaia.imoviesapp.utils.createToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_single_title.*
import kotlinx.android.synthetic.main.tv_details_fragment.*

//class TvDetailsFragment : Fragment(R.layout.tv_details_fragment) {
//    private lateinit var viewModel: SingleTitleViewModel
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(this).get(SingleTitleViewModel::class.java)
//        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
//        viewModel.getSingleTitleFiles(titleId)
//
//        viewModel.singleTitleFiles.observe(viewLifecycleOwner, {
//            if (it.posters.data != null) {
//                Picasso.get().load(it.posters.data.x240).into(tv_single_poster)
//            }
//            tv_single_geo_name.text = it.primaryName
//            tv_single_eng_name.text = it.secondaryName
//            tv_single_year.text = it.year.toString()
//
//            if (it.rating?.imdb?.score != null) {
//                tv_single_imdb_score.text = it.rating.imdb.score.toString()
//            }
//        })
//    }
//}

class TvDetailsFragment : DetailsSupportFragment() {
    private lateinit var viewModel: SingleTitleViewModel
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SingleTitleViewModel::class.java)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        viewModel.getSingleTitleFiles(titleId)

        viewModel.singleTitleFiles.observe(viewLifecycleOwner, {
            buildDetails(it)
        })
    }

    private fun buildDetails(details: TitleData.Data) {
        val actionAdapter = ArrayObjectAdapter()
        val selector = ClassPresenterSelector().apply {
            FullWidthDetailsOverviewRowPresenter(TvDetailsPresenter()).also {
                addClassPresenter(DetailsOverviewRow::class.java, it)
                it.backgroundColor = ContextCompat.getColor(requireContext(), R.color.green_dark)
                it.onActionClickedListener = OnActionClickedListener { action ->
                    if (action.id == 1.toLong()) {
                        val intent = Intent(context, TvChooseFilesActivity::class.java)
                        intent.putExtra("titleId", details.id)
                        intent.putExtra("isTvShow", details.isTvShow)
                        activity?.startActivity(intent)
                    }
                }
            }
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
        rowsAdapter = ArrayObjectAdapter(selector)

        val detailsOverview = DetailsOverviewRow(details).apply {
            Glide.with(requireContext()).asDrawable().load(details.posters.data?.x240)
                    .into(object : SimpleTarget<Drawable>(374, 374) {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            imageDrawable = resource
                        }

                    })

            actionAdapter.add(Action(1, "Play"))
            actionAdapter.add(Action(2, "Trailer"))
            actionsAdapter = actionAdapter
        }
        rowsAdapter.add(detailsOverview)

//        // Add a Related items row
//        val listRowAdapter = ArrayObjectAdapter(ListRowPresenter()).apply {
//            add("Media Item 1")
//            add("Media Item 2")
//            add("Media Item 3")
//        }
//        val header = HeaderItem(0, "Related Items")
//        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter
    }
}