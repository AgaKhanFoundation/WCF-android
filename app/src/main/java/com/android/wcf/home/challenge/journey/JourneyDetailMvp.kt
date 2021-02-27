package com.android.wcf.home.challenge.journey

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Milestone

interface JourneyDetailMvp {
    interface View : BaseMvp.BaseView {
        fun showMilestoneDetail(milestone: Milestone)
    }

    interface Presenter : BaseMvp.Presenter {
    }

    interface Host : BaseMvp.Host {

    }
}