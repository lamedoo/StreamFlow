package com.lukakordzaia.streamflowphone.ui.baseclasses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.lukakordzaia.core.sharedpreferences.SharedPreferences
import org.koin.android.ext.android.inject

abstract class BaseActivity<VB: ViewBinding> : AppCompatActivity() {
    protected val sharedPreferences: SharedPreferences by inject()

    lateinit var binding: VB
    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
    }
}