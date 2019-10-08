package com.android.wcf.settings;

import com.android.wcf.model.Participant;

import java.util.List;

public interface TeamMembershipAdapterMvp {
    public interface View {
        void updateParticipantsData(List<Participant> participantList);
        void participantsDataUpdated();

        void updateEditMode(boolean editModeFlag);
        void editModeUpdated(boolean editModeFlag);

        void clearSelectionPosition();
    }

    public interface Presenter {
        int getParticipantsCount();

        void updateParticipantsData(List<Participant> participantList);

        Participant getParticipant(int position);

        void removeMemberFromTeam( int pos);

        void updateEditMode(boolean editModeFlag);
    }

    public interface Host {
        void removeMemberFromTeam(String participantName, String participantId);
    }
}