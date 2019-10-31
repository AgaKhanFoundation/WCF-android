package com.android.wcf.application;

import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Badge;
import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataHolder {
    private static final String TAG = DataHolder.class.getSimpleName();
    private static Team participantTeam;
    private static Participant participant;
    private static Event event;
    private static List<Team> teams;
    private static ArrayList<Badge> dailyThresholdBadgeList;
    private static ArrayList<Badge> challengeBadgeList;
    private static boolean eventEndedForBadges;
    private static Date lastBadgeSavedAt;
    private static int akfProfileShowCount = 0;

    public static Event getEvent() {
        return event;
    }

    public static void setEvent(Event event) {
        DataHolder.event = event;
        updateParticipantCommitmentFromEvent(); //TODO: check if this overkill. setParticipant updates it
    }

    public static Team getParticipantTeam() {
        return participantTeam;
    }

    public static void setParticipantTeam(Team participantTeam) {
        DataHolder.participantTeam = participantTeam;
    }

    public static Participant getParticipant() {
        return participant;
    }

    public static void setParticipant(Participant participant) {
        DataHolder.participant = participant;
        updateParticipantCommitmentFromEvent();
    }

    public static List<Team> getTeamsList() {
        return teams;
    }

    public static void setTeamsList(List<Team> teams) {
        DataHolder.teams = teams;
    }


    public static void updateParticipantCommitmentInCachedTeam(int steps) {
        if (participant == null) {
            return;
        }
        String participantId = participant.getParticipantId();
        if (participantTeam != null && participantId != null) {
            for (Participant participant : participantTeam.getParticipants()) {
                if (participant.getParticipantId().equals(participantId)) {
                    if (participant.getCommitment() != null) {
                        participant.getCommitment().setCommitmentSteps(steps);
                    }
                }
            }
        }
    }

    public static void updateParticipantCommitment(int steps) {
        if (participant == null) {
            return;
        }
        if (event == null) {
            return;
        }

        Event participantEvent = participant.getEvent(event.getId());
        if (participantEvent != null) {
            Commitment commitment = participantEvent.getParticipantCommitment();
            if (commitment != null) {
                commitment.setCommitmentSteps(steps);
            }
        }

        Commitment commitment = participant.getCommitment();
        if (commitment != null) {
            commitment.setCommitmentSteps(steps);
        }
    }

    public static void updateParticipantCommitmentFromEvent() {
        if (participant == null) {
            return;
        }
        if (event == null) {
            return;
        }

        Event participantEvent = participant.getEvent(event.getId());
        if (participantEvent != null) {
            Commitment commitment = participantEvent.getParticipantCommitment();
            if (commitment != null) {
                participant.setCommitment(commitment);
                if (commitment.getCommitmentSteps() == 0) {
                    commitment.setCommitmentSteps(SharedPreferencesUtil.getMyStepsCommitted());
                }
            }
        }
    }

    public static void clearCache() {
        event = null;
        participant = null;
        participantTeam = null;
        teams = null;
        dailyThresholdBadgeList = null;
        challengeBadgeList = null;
        eventEndedForBadges = false;
    }

    public static void updateParticipantTeamName(String newName) {
        if (participantTeam != null) {
            participantTeam.setName(newName);
        }
    }


    public static void saveBadgesEarned(ArrayList<Badge> challengeBadgeList, ArrayList<Badge> dailyBadgeList, boolean eventEndedForBadges) {
        DataHolder.challengeBadgeList = challengeBadgeList;
        DataHolder.dailyThresholdBadgeList = dailyBadgeList;
        DataHolder.eventEndedForBadges = eventEndedForBadges;
        DataHolder.lastBadgeSavedAt = new Date();
    }

    public static ArrayList<Badge> getDailyThresholdBadgeList() {
        return dailyThresholdBadgeList;
    }

    public static ArrayList<Badge> getChallengeBadgeList() {
        return challengeBadgeList;
    }

    public static boolean getEventEndedForBadges() {
        return eventEndedForBadges;
    }

    public static Date getLastBadgeSavedAt() {
        return lastBadgeSavedAt;
    }

    public static boolean checkShowAkfProfile() {
        akfProfileShowCount++;
        //will remind upto 4 times in a session
        return (akfProfileShowCount <= 20 && (akfProfileShowCount ==1 ||  akfProfileShowCount % 5 == 0));
    }
}
