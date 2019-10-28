package com.android.wcf.home.dashboard

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Badge
import com.android.wcf.model.Event
import com.android.wcf.model.Participant
import com.android.wcf.model.Team

interface BadgesMvp {
    interface View: BaseMvp.BaseView {

        fun onNoBadgesData(challengeEnded:Boolean)
        fun onBadgesData(challengeBadges:List<Badge>, dailyGoalBadges:List<Badge>, challengeEnded:Boolean)
    }

    interface Presenter : BaseMvp.Presenter {
        fun getBadgesData(event:Event, team:Team, participant:Participant )
    }

    interface Host : BaseMvp.Host{

    }
}