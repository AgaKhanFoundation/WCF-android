package com.android.wcf.home.challenge

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Team

interface CreateTeamMvp {
    interface View : BaseMvp.BaseView {
        fun teamCreated(team: Team)
        fun participantJoinedTeam(participantId: String, teamId: Int)
        fun showCreateTeamConstraintError()
        fun confirmCancelTeamCreation()
        fun onAssignParicipantToTeamError(error: Throwable, participantId: String, teamId: Int);
        fun onCreateTeamError(error: Throwable);
    }

    interface Presenter : BaseMvp.Presenter {
        fun createTeam(teamName: String, teamLeadParticipantId: String, teamVisibility: Boolean? = true)
        fun assignParticipantToTeam(participantId: String, teamId: Int)
    }

    interface Host : BaseMvp.Host {

    }
}