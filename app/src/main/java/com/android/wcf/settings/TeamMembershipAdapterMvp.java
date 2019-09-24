package com.android.wcf.settings;

import com.android.wcf.model.Participant;

import java.util.List;

public interface TeamMembershipAdapterMvp {
    public interface View {
        void updateParticipantsData(List<Participant> participantList);
        void clearSelectionPosition();
        void participantsDataUpdated();
    }

    public interface Presenter {
        int getParticipantsCount();

        void updateParticipantsData(List<Participant> participantList);

        Participant getParticipant(int position);

        void removeMemberFromTeam( int pos);
    }

    public interface Host {
        void removeMemberFromTeam(String participantName, String participantId);
    }
}