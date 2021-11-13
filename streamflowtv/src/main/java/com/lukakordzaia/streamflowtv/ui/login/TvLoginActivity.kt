package com.lukakordzaia.streamflowtv.ui.login

import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseFragmentActivity
import com.lukakordzaia.streamflowtv.databinding.ActivityTvLoginBinding

class TvLoginActivity: BaseFragmentActivity<ActivityTvLoginBinding>() {
    override fun getViewBinding() = ActivityTvLoginBinding.inflate(layoutInflater)
}