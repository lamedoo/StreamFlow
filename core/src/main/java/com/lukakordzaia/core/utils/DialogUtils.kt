package com.lukakordzaia.core.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import com.lukakordzaia.core.R
import com.lukakordzaia.core.databinding.DialogDownloadReleaseBinding
import com.lukakordzaia.core.databinding.DialogGeneralAlertBinding

object DialogUtils {
    fun generalAlertDialog(
        context: Context,
        title: Int? = null,
        image: Int? = null,
        confirm: () -> Unit
    ): Dialog {
        val binding = DialogGeneralAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.6F)
        dialog.setContentView(binding.root)

        title?.let {
            binding.title.text = context.getString(title)
        }

        image?.let {
            binding.icon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, image, null))
        }

        binding.continueButton.setOnClickListener {
            confirm.invoke()
            dialog.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        binding.continueButton.requestFocus()
        return dialog
    }

    fun downloadReleaseAlertDialog(
        context: Context,
        confirm: () -> Unit
    ): Dialog {
        val binding = DialogDownloadReleaseBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setDimAmount(0.6F)
            setContentView(binding.root)
        }

        binding.continueButton.setOnClickListener {
            confirm.invoke()
            binding.downloadLoader.setVisible()
            binding.pleaseWait.setVisible()
            binding.icon.setInvisible()
            binding.cancelButton.setInvisible()
            binding.continueButton.setInvisible()
        }
        binding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        binding.continueButton.requestFocus()
        return dialog
    }
}