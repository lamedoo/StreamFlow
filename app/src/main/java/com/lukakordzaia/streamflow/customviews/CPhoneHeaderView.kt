package com.lukakordzaia.streamflow.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.PhoneHeaderLayoutBinding
import com.lukakordzaia.streamflow.utils.setVisibleOrGone

class CPhoneHeaderView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    val binding = PhoneHeaderLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    var header = ""

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CPhoneHeaderView, 0, 0).apply {
            try {
                header = getString(R.styleable.CPhoneHeaderView_title).toString()
                if (header != "null") {
                    binding.header.text = header
                }

                binding.moreButton.setVisibleOrGone(getBoolean(R.styleable.CPhoneHeaderView_arrow_visibility, false))
            } finally {
                recycle()
            }
        }
    }
}