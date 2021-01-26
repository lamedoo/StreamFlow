package com.lukakordzaia.imoviesapp.ui.phone.settings

import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.setInvisible
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun setUpCircularAnimation() {
        settings_root.setInvisible()
        settings_root.doOnLayout {
            revealCircular()
        }
    }

    private fun revealCircular() {
        val rootHeight = settings_root.height

        val centerX = settings_reveal_button.x
        val centerY = settings_reveal_button.y / 2

        val circularReveal = ViewAnimationUtils.createCircularReveal(
                settings_root,
                centerX.toInt(),
                centerY.toInt(),
                0f,
                rootHeight.toFloat() * 2f
        )

        circularReveal.duration = 1000

        settings_root.setVisible()
        settings_root.setBackgroundColor(resources.getColor(R.color.black))
        circularReveal.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        setUpCircularAnimation()

        settings_delete_history.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setMessage("ნამდვილად გსურთ ისტორიის წაშლა?")
                    .setCancelable(false)
                    .setPositiveButton("დიახ") { _, _ ->
                        viewModel.deleteWatchedHistory(requireContext())
                        viewModel.onDeletePressed(requireContext())
                    }
                    .setNegativeButton("არა") { dialog, _ ->
                        dialog.dismiss()
                    }
            val alert = alertDialog.create()
            alert.show()
        }
    }
}