package com.android.wcf.application;

import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

import java.util.List;

public class DataHolder {
   private static Team participantTeam;
   private static Participant participant;
   private static Event event;
   private static List<Team> teams;

    public static Event getEvent() {
        return event;
    }

    public static void setEvent(Event event) {
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
}
