package com.lukakordzaia.core.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import com.lukakordzaia.core.databinding.DialogGeneralAlertBinding

object DialogUtils {
    fun generalAlertDialog(
        context: Context,
        title: Int? = null,
        image: Int? = null,
        confirm: () -> Unit
    ): Dialog {
        val binding = DialogGeneralAlertBinding.inflate(LayoutInflater.from(context))
        val removeTitle = Dialog(context)
        removeTitle.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        removeTitle.window?.setDimAmount(0.6F)
        removeTitle.setContentView(binding.root)

        title?.let {
            binding.title.text = context.getString(title)
        }

        image?.let {
            binding.icon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, image, null))
        }

        binding.continueButton.setOnClickListener {
            confirm.invoke()
            removeTitle.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            removeTitle.dismiss()
        }
        removeTitle.show()
        binding.continueButton.requestFocus()
        return removeTitle
    }
}