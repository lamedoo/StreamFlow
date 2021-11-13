package com.lukakrodzaia.streamflowtv.ui.login

import com.lukakrodzaia.streamflowtv.baseclasses.activities.BaseFragmentActivity
import com.lukakrodzaia.streamflowtv.databinding.ActivityTvLoginBinding

class TvLoginActivity: BaseFragmentActivity<ActivityTvLoginBinding>() {
    override fun getViewBinding() = ActivityTvLoginBinding.inflate(layoutInflater)
}