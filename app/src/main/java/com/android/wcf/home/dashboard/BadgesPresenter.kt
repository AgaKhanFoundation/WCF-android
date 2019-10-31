package com.android.wcf.home.dashboard

import com.android.wcf.application.DataHolder
import com.android.wcf.helper.DistanceConverter
import com.android.wcf.home.BasePresenter
import com.android.wcf.model.*
import java.util.*
import kotlin.collections.ArrayList

class BadgesPresenter(val view: BadgesMvp.View) : BasePresenter(), BadgesMvp.Presenter {

    override fun getBadgesData(event: Event, team: Team, participant: Participant) {

        if (event == null || team == null || participant == null) {
            view.onNoBadgesData(false)
            return
        }

        var challengeEnded = event.hasChallengeEnded()
        var dailyThresholdBadges = arrayListOf<Badge>()
        var challengeBadges = arrayListOf<Badge>()
        var now = Date()

        //optimize the badge assignment for the session.
        var lastBadgeSaveAt = DataHolder.getLastBadgeSavedAt() ?: Date();
        if (now.time - lastBadgeSaveAt.time < Constants.BADGE_CALCULATION_DELTA_MIN * 60 * 1000 ) {

             dailyThresholdBadges = DataHolder.getDailyThresholdBadgeList()
                    ?: arrayListOf()
             challengeBadges = DataHolder.getChallengeBadgeList() ?: arrayListOf()

            if (dailyThresholdBadges.isNotEmpty() && challengeBadges.isNotEmpty() && DataHolder.getEventEndedForBadges() == challengeEnded) {
                view.onBadgesData(challengeBadges, dailyThresholdBadges, challengeEnded)
                return
            }
        }

        loadParticipantBadges(participant, event, challengeBadges, dailyThresholdBadges, challengeEnded)

        val teamBadge = getTeamBadge(event, team)
        teamBadge?.let { challengeBadges.add(it) }

        if (challengeBadges.isEmpty() && dailyThresholdBadges.isEmpty()) {
            view.onNoBadgesData(challengeEnded)
        } else {
            DataHolder.saveBadgesEarned(challengeBadges, dailyThresholdBadges, challengeEnded)
            view.onBadgesData(challengeBadges, dailyThresholdBadges, challengeEnded)
        }
    }

    private fun loadParticipantBadges(participant: Participant
                                      , event: Event
                                      , challengeBadges: ArrayList<Badge>
                                      , dailyGoalBadges: ArrayList<Badge>, challengeEnded:Boolean) {
        val recordsList = participant.records?.sortedBy { record -> record.date }

        addDistanceBadge(recordsList, challengeBadges)

        var daysWith10KStepsMet = recordsList.filter {
            it.distance ?: 0 >= BadgeType.DAILY_10K_STEPS.threshold
        }
        if (challengeEnded) {
            if (daysWith10KStepsMet.size >= BadgeType.LEVEL_CHAMPION.threshold) {
                dailyGoalBadges.add(Badge(BadgeType.LEVEL_CHAMPION, event.endDate?.time))
            } else if (daysWith10KStepsMet.size >= BadgeType.LEVEL_PLATINUM.threshold) {
                dailyGoalBadges.add(Badge(BadgeType.LEVEL_PLATINUM, event.endDate?.time))
            } else if (daysWith10KStepsMet.size >= BadgeType.LEVEL_GOLD.threshold) {
                dailyGoalBadges.add(Badge(BadgeType.LEVEL_GOLD, event.endDate?.time))
            } else if (daysWith10KStepsMet.size >= BadgeType.LEVEL_SILVER.threshold) {
                dailyGoalBadges.add(Badge(BadgeType.LEVEL_SILVER, event.endDate?.time))
            }
        }

        var dailyStreakDays = 0
        if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_90_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_90_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_90_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_80_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_80_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_80_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_70_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_70_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_70_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_60_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_60_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_60_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_50_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_50_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_50_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_40_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_40_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_40_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_30_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_30_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_30_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_20_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_20_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_20_DAYS_10K_STEPS_PER_DAY.threshold
        } else if (addStreakBadge(daysWith10KStepsMet,
                        BadgeType.STREAK_10_DAYS_10K_STEPS_PER_DAY.threshold,
                        BadgeType.STREAK_10_DAYS_10K_STEPS_PER_DAY, challengeBadges)) {
            dailyStreakDays = BadgeType.STREAK_10_DAYS_10K_STEPS_PER_DAY.threshold
        }

        if (dailyStreakDays > 0) {
            //remove days already credit for in the streak badge. The remaining days will get their individual badges
            val latestRecord = daysWith10KStepsMet.get(dailyStreakDays - 1)
            daysWith10KStepsMet = daysWith10KStepsMet.filter { it.date!! > latestRecord.date!! }
        }

        // If a level badge was earned, no need for daily steps badge
        if (dailyGoalBadges.isEmpty()) {
            for (record in daysWith10KStepsMet) {
                if (record.distance ?: 0 >= BadgeType.DAILY_25K_STEPS.threshold) {
                    dailyGoalBadges.add(Badge(BadgeType.DAILY_25K_STEPS, record.date))
                } else if (record.distance ?: 0 >= BadgeType.DAILY_20K_STEPS.threshold) {
                    dailyGoalBadges.add(Badge(BadgeType.DAILY_20K_STEPS, record.date))
                } else if (record.distance ?: 0 >= BadgeType.DAILY_15K_STEPS.threshold) {
                    dailyGoalBadges.add(Badge(BadgeType.DAILY_15K_STEPS, record.date))
                } else if (record.distance ?: 0 >= BadgeType.DAILY_10K_STEPS.threshold) {
                    dailyGoalBadges.add(Badge(BadgeType.DAILY_10K_STEPS, record.date))
                }
            }
        }
    }

    private fun addDistanceBadge(recordsList: List<Record>, challengeBadges: ArrayList<Badge>) {
        val totalStepsWalked = recordsList.sumBy { it.distance ?: 0 }
        var badgeTypeEarned = BadgeType.UNKNOWN

        if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_500.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_500
        } else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_450.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_450
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_400.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_400
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_350.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_350
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_300.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_300
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_250.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_250
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_200.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_200
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_150.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_150
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_100.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_100
        }else if (totalStepsWalked >= DistanceConverter.steps(BadgeType.DISTANCE_COMPLETED_50.threshold)) {
            badgeTypeEarned = BadgeType.DISTANCE_COMPLETED_50
        }

        if (badgeTypeEarned != BadgeType.UNKNOWN) {

            var cumulativeDistance = 0
            //find the date when the distance level was reached
            for (record in recordsList) {
                cumulativeDistance += record.distance ?: 0

                if (cumulativeDistance >= DistanceConverter.steps(badgeTypeEarned.threshold)) {
                    challengeBadges.add(Badge(badgeTypeEarned, record?.date))
                    break
                }
            }
        }
    }

    private fun getTeamBadge(event: Event, team: Team): Badge? {
        return null
    }

    private fun addStreakBadge(daysWithStepsGoalMet: List<Record>, streakLength: Int, badgeType: BadgeType, badges: ArrayList<Badge>): Boolean {
        if (daysWithStepsGoalMet.size >= streakLength) {
            badges.add(Badge(badgeType, daysWithStepsGoalMet.get(streakLength - 1).date))

            return true
        }
        return false
    }

}