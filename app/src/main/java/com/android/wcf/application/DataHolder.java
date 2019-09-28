package com.android.wcf.application;

import android.util.Log;

import com.android.wcf.model.Commitment;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

import java.util.List;

public class DataHolder {
    private static final String TAG = DataHolder.class.getSimpleName();
    private static Team participantTeam;
    private static Participant participant;
    private static Event event;
    private static List<Team> teams;

    public static Event getEvent() {
        return event;
    }

    public static void setEvent(Event event) {
        if (DataHolder.event != null && event != null) {

            //Don't overwrite if cached version already has participant commitment and new instance for the same event will make us loose it.
            // This is workaround to ensure an event refresh from somewhere in the app does not overwrite the event instance from Participant

            if (event.getId() == DataHolder.event.getId()) {
                Commitment cachedCommitment = DataHolder.event.getParticipantCommitment();
                Commitment commitment = event.getParticipantCommitment();
                if (cachedCommitment != null && cachedCommitment.getId() > 0
                        && (commitment == null || commitment.getId() <= 0) ) {
                    Log.i(TAG, "event cache request ignored since the new event does not ave participant commitment");
                    return;
                }
            }
        }
        DataHolder.event = event;
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
    }

    public static List<Team> getTeams() {
        return teams;
    }

    public static void setTeams(List<Team> teams) {
        DataHolder.teams = teams;
    }

    public static void updateParticipantCommittedDistance(int distance) {
        if (participant != null) {
            participant.setCommitmentDistance(distance);
        }
        //TODO update team's commitment
    }
}
