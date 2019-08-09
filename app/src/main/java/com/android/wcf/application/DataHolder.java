package com.android.wcf.application;

import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

public class DataHolder {
   private static Team participantTeam;
   private static Participant participant;

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
}
