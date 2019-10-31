package com.android.wcf.home.dashboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.wcf.base.BaseMvp
import com.android.wcf.model.*
import java.util.*

interface BadgesMvp {
    interface View: BaseMvp.BaseView {

        fun onNoBadgesData(challengeEnded:Boolean)
        fun onBadgesData(challengeBadges:List<Badge>, dailyGoalBadges:List<Badge>, challengeEnded:Boolean)
        fun showBadgeDetail(badge:Badge)
    }

    interface Presenter : BaseMvp.Presenter {
        fun getBadgesData(event:Event, team:Team, participant:Participant )
    }

    interface Host : BaseMvp.Host{
    }
}