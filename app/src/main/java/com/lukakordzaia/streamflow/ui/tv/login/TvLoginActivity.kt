package com.lukakordzaia.streamflow.ui.tv.login

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.databinding.ActivityTvLoginBinding

class TvLoginActivity: FragmentActivity() {
    private lateinit var binding: ActivityTvLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


}