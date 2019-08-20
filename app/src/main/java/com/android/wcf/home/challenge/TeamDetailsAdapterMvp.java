package com.android.wcf.home.challenge;

import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;

import java.util.List;

public interface TeamDetailsAdapterMvp {
    public interface View {
        void updateParticipantsData(List<Participant> participantList);
        void clearSelectionPosition();
        void participantsDataUpdated();
    }

    public interface Presenter {
        int getParticipantsCount();

        void updateParticipantsData(List<Participant> participantList);

        Participant getParticipant(int position);
    }

    public interface Host {
    }
}