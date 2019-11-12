package com.android.wcf.home.challenge.journey

import com.android.wcf.application.DataHolder
import com.android.wcf.helper.DistanceConverter
import com.android.wcf.home.BasePresenter

class JourneyPresenter(val view: JourneyMvp.View) : BasePresenter(), JourneyMvp.Presenter {
    private var milestonesCompleted = 0
    var milestoneTotalDistanceMi: Long = 0
    var milestoneRemainingDistanceMi: Long = 0

    override fun getMilestonesData() {
        val team = DataHolder.getParticipantTeam()
        val journeyMilestones = DataHolder.getMilestones()
        if (team != null) {
            milestonesCompleted = 0
            val teamStepsCompleted = team.geTotalParticipantCompletedSteps()
            for (milestone in journeyMilestones) {
                val reached = milestone.hasReached(teamStepsCompleted)
                if (reached) milestonesCompleted++
            }
            //subtract
            milestonesCompleted--
            val prevMilestone = journeyMilestones[milestonesCompleted]
            val currentMilestone = journeyMilestones[milestonesCompleted + 1]
            val milestoneTotalDistanceSteps = currentMilestone.steps - prevMilestone.steps
            milestoneTotalDistanceMi = DistanceConverter.distance(milestoneTotalDistanceSteps)
            milestoneRemainingDistanceMi = DistanceConverter.distance(teamStepsCompleted - prevMilestone.steps)

            view.showJourneyOverview(milestoneRemainingDistanceMi, milestoneTotalDistanceMi, currentMilestone.name)

            view.showMilestoneData(journeyMilestones, milestonesCompleted + 1, milestoneRemainingDistanceMi.toDouble() / milestoneTotalDistanceMi)
        }
    }

    override fun onMilestoneSelected() {
        view.showMilestoneDetail()
    }
}