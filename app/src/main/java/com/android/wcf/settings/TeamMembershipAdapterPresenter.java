package com.android.wcf.settings;

import androidx.annotation.NonNull;

import com.android.wcf.model.Participant;

import java.util.ArrayList;
import java.util.List;

public class TeamMembershipAdapterPresenter implements TeamMembershipAdapterMvp.Presenter {

    TeamMembershipAdapterMvp.View view;
    List<Participant> participants = new ArrayList<>();
    TeamMembershipAdapterMvp.Host host;

    public TeamMembershipAdapterPresenter(@NonNull TeamMembershipAdapterMvp.View view, @NonNull TeamMembershipAdapterMvp.Host host) {
        this.view = view;
        this.host = host;
    }

    @Override
    public int getParticipantsCount() {
        return participants.size();
    }

    @Override
    public void updateParticipantsData(List<Participant> participantList) {
        this.participants.clear();
        this.participants.addAll(participantList);
        view.participantsDataUpdated();
    }

    @Override
    public Participant getParticipant(int position) {
        return participants.get(position);
    }

    @Override
    public void removeMemberFromTeam(int pos) {
        Participant participant = getParticipant(pos);
        host.removeMemberFromTeam(participant.getName(), participant.getParticipantId());
    }
}
