package com.lukakordzaia.streamflow.ui.tv.login

import com.lukakordzaia.streamflow.databinding.ActivityTvLoginBinding
import com.lukakordzaia.streamflow.ui.baseclasses.activities.BaseFragmentActivity

class TvLoginActivity: BaseFragmentActivity<ActivityTvLoginBinding>() {
    override fun getViewBinding() = ActivityTvLoginBinding.inflate(layoutInflater)
}