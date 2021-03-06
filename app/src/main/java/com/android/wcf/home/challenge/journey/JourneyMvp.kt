package com.android.wcf.home.challenge.journey

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Milestone

interface JourneyMvp {
    interface View : BaseMvp.BaseView {
        fun showMilestoneData(milestones: List<Milestone>, lastCompletedMilestoneSequence: Int, nextMilestoneSequence: Int, nextMilestonePercentageCompletion: Double)
        fun showJourneyOverview(completedMiles: Long, totalMiles: Long, nextMilestoneName: String)
    }

    interface Presenter : BaseMvp.Presenter {
        fun getMilestonesData()
    }

    interface Host : BaseMvp.Host {
        fun showMilestoneDetail(milestone: Milestone)
    }
}