package com.android.wcf.home.challenge.journey

import com.android.wcf.application.DataHolder
import com.android.wcf.helper.DistanceConverter
import com.android.wcf.home.BasePresenter

class JourneyPresenter(val view: JourneyMvp.View) : BasePresenter(), JourneyMvp.Presenter {
    private var milestonesCompleted = 0
    var nextMilestoneDistance: Long = 0
    var nextMilestoneRemainingDistance: Long = 0

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
            //journeyMilestones[0] is just a start placeholder, so reduce the completed count
            if (milestonesCompleted > 0)
                milestonesCompleted--
            val lastCompletedMilestone = journeyMilestones[milestonesCompleted]
            val nextMilestone =
                    if (milestonesCompleted < journeyMilestones.size - 1)
                        journeyMilestones[milestonesCompleted + 1]
                    else journeyMilestones[milestonesCompleted]

            val nextMilestoneSteps = nextMilestone.steps - lastCompletedMilestone.steps
            val nextMilestoneRemainingSteps = nextMilestone.steps - teamStepsCompleted
            val nextMilestoneJourneyPctCompleted = if (nextMilestoneSteps == 0)  100.0 else
                (teamStepsCompleted - lastCompletedMilestone.steps) * 100.0 / nextMilestoneSteps

            nextMilestoneDistance = DistanceConverter.distance(nextMilestoneSteps)
            nextMilestoneRemainingDistance = DistanceConverter.distance(nextMilestoneRemainingSteps)
            if (nextMilestoneRemainingDistance < 0) nextMilestoneRemainingDistance = 0

            view.showJourneyOverview(nextMilestoneRemainingDistance, nextMilestoneDistance, nextMilestone.name)

            view.showMilestoneData(journeyMilestones, milestonesCompleted, nextMilestoneJourneyPctCompleted)
        }
    }

    override fun onMilestoneSelected() {
        view.showMilestoneDetail()
    }
}