package com.lukakrodzaia.streamflowtv.baseclasses.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.core.sharedpreferences.SharedPreferences
import org.koin.android.ext.android.inject

abstract class BaseFragmentActivity<VB : ViewBinding> : FragmentActivity() {
    protected val sharedPreferences: SharedPreferences by inject()

    lateinit var binding: VB
    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
    }
}