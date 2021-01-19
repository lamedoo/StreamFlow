package com.lukakordzaia.imoviesapp.ui.tv.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.TitleData
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles.TvChooseFilesActivity

class TvDetailsFragment : DetailsSupportFragment() {
    private lateinit var viewModel: SingleTitleViewModel
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SingleTitleViewModel::class.java)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        viewModel.getSingleTitleData(titleId)

        viewModel.singleTitleData.observe(viewLifecycleOwner, {
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
                        intent.putExtra("numOfSeasons", details.seasons?.data?.size)
                        intent.putExtra("titlePoster", details.posters.data?.x240)
                        intent.putExtra("titleName", details.secondaryName)
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