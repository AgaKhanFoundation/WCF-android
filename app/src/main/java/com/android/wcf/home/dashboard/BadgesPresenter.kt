package com.android.wcf.home.dashboard

import com.android.wcf.home.BasePresenter
import com.android.wcf.model.Badge
import com.android.wcf.model.Event
import com.android.wcf.model.Participant
import com.android.wcf.model.Team

class BadgesPresenter(val view:BadgesMvp.View):BasePresenter(), BadgesMvp.Presenter {

    override fun getBadgesData(event: Event, team: Team, participant: Participant) {

        if (event == null || team == null || participant == null) {
            view.onNoBadgesData()
            return
        }
        val badges = listOf<Badge>()

        if (badges.isEmpty()) {
            view.onNoBadgesData()
        }
    }
}