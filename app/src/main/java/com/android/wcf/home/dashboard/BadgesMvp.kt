package com.android.wcf.home.dashboard

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Event
import com.android.wcf.model.Participant
import com.android.wcf.model.Team

interface BadgesMvp {
    interface View: BaseMvp.BaseView {

        fun onNoBadgesData()
    }

    interface Presenter : BaseMvp.Presenter {
        fun getBadgesData(event:Event, team:Team, participant:Participant )
    }

    interface Host : BaseMvp.Host{

    }
}