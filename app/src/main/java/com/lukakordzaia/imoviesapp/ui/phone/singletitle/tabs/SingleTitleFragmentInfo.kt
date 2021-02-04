package com.lukakordzaia.imoviesapp.ui.phone.singletitle.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import kotlinx.android.synthetic.main.phone_single_title_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleTitleFragmentInfo : Fragment(R.layout.phone_single_title_info) {
    private val viewModel by viewModel<SingleTitleViewModel>()
    private var titleId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            val id = bundle.getString("titleId")
            titleId = id!!.toInt()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSingleTitleData(titleId)

        viewModel.singleTitleData.observe(viewLifecycleOwner, {

            if (it.plot.data != null) {
                if (!it.plot.data.description.isNullOrEmpty()) {
                    single_title_desc.text = it.plot.data.description
                } else {
                    single_title_desc.text = "აღწერა არ მოიძებნა"
                }
            } else {
                single_title_desc.text = "აღწერა არ მოიძებნა"
            }

            tv_single_title_year.text = it.year.toString()
            tv_single_title_duration.text = "${it.duration} წ."
            if (!it.countries.data.isNullOrEmpty()) {
                tv_single_title_country.text = it.countries.data[0].secondaryName
            }

        })
    }
}