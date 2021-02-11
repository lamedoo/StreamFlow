package com.lukakordzaia.medootv.ui.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.medootv.R

class TvActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)
    }
}