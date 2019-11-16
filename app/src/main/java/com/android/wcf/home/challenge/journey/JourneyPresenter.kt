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

        val teamStepsCompleted: Int = team?.geTotalParticipantCompletedSteps() ?: 0
        milestonesCompleted = journeyMilestones?.filter { milestone -> milestone.reached }?.map { milestone -> milestone.sequence }?.max()
                ?: 0
        //journeyMilestones[0] is just a start placeholder, so reduce the completed count
//        if (milestonesCompleted > 0)
//            milestonesCompleted--

        val lastCompletedMilestone = journeyMilestones[milestonesCompleted]
        val nextMilestone =
                if (milestonesCompleted < journeyMilestones.size - 1)
                    journeyMilestones[milestonesCompleted + 1]
                else journeyMilestones[milestonesCompleted]

        val nextMilestoneSteps = nextMilestone.steps - lastCompletedMilestone.steps
        val nextMilestoneRemainingSteps = nextMilestone.steps - teamStepsCompleted

        nextMilestoneDistance = DistanceConverter.distance(nextMilestoneSteps)
        nextMilestoneRemainingDistance = DistanceConverter.distance(nextMilestoneRemainingSteps)
        if (nextMilestoneRemainingDistance < 0) nextMilestoneRemainingDistance = 0

        view.showJourneyOverview(nextMilestoneRemainingDistance, nextMilestoneDistance, nextMilestone.name)

        val nextMilestoneJourneyPctCompleted =
                if (teamStepsCompleted <= 0) 0.0
                else if (nextMilestoneSteps == 0) 100.0
                else (teamStepsCompleted - lastCompletedMilestone.steps) * 100.0 / nextMilestoneSteps

        view.showMilestoneData(journeyMilestones, lastCompletedMilestone.sequence, nextMilestone.sequence, nextMilestoneJourneyPctCompleted)

    }

    override fun onMilestoneSelected() {
        view.showMilestoneDetail()
    }
}