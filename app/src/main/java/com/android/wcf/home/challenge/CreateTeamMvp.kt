package com.android.wcf.home.challenge

import com.android.wcf.base.BaseMvp
import com.android.wcf.model.Team

interface CreateTeamMvp {
    interface View : BaseMvp.BaseView {
         fun teamCreated(team: Team)
        fun participantJoinedTeam(participantId:String, teamId:Int)

    }
    interface Presenter : BaseMvp.Presenter {
         fun createTeam(teamName: String, teamLeadParticipantId: String, teamVisibility: Boolean? = true)
    }

    interface Host: BaseMvp.Host {
        fun setToolbarTitle(title: String)
    }
}