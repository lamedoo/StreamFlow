package com.lukakordzaia.streamflow.ui.phone.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.navController
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.phone_continue_watching_info_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContinueWatchingInfoFragment : BottomSheetDialogFragment() {
    private val homeViewModel: HomeViewModel by viewModel()
    private val args: ContinueWatchingInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        return inflater.inflate(R.layout.phone_continue_watching_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continue_watching_info_title.text = args.titleName

        continue_watching_info_close.setOnClickListener {
            dismiss()
        }

        continue_watching_info_details.setOnClickListener {
            homeViewModel.onSingleTitlePressed(AppConstants.NAV_CONTINUE_WATCHING_TO_SINGLE, args.titleId)
        }

        continue_watching_info_remove.setOnClickListener {
            val clearDbDialog = Dialog(requireContext())
            clearDbDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
            clearDbDialog.clear_db_alert_yes.setOnClickListener {
                homeViewModel.deleteContinueWatching(requireContext(), args.titleId)
                clearDbDialog.dismiss()
                dismiss()
            }
            clearDbDialog.clear_db_alert_no.setOnClickListener {
                clearDbDialog.dismiss()
            }
            clearDbDialog.show()
        }

        homeViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        homeViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }
}