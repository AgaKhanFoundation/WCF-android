package com.android.wcf.home.challenge.journey

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Milestone

interface JourneyMvp {
    interface View: BaseMvp.BaseView {
        fun showMilestoneDetail()
        fun showMilestoneData(milestones: List<Milestone>, currentMilestoneSequence: Int, currentMilestonePercentageCompletion: Double)
        fun showJourneyOverview(completedMiles: Long, totalMiles: Long, nextLocation: String)

//        fun onNoBadgesData(challengeEnded:Boolean)
    }

    interface Presenter : BaseMvp.Presenter {
        fun onMilestoneSelected()
        fun getMilestonesData()
    }

    interface Host : BaseMvp.Host{

    }
}