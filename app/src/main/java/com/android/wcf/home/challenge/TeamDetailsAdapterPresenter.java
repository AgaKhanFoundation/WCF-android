package com.android.wcf.home.challenge;

import androidx.annotation.NonNull;

import com.android.wcf.model.Participant;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailsAdapterPresenter implements TeamDetailsAdapterMvp.Presenter {

    TeamDetailsAdapterMvp.View view;
    List<Participant> participants = new ArrayList<>();
    TeamDetailsAdapterMvp.Host host;

    public TeamDetailsAdapterPresenter(@NonNull TeamDetailsAdapterMvp.View view, @NonNull TeamDetailsAdapterMvp.Host host) {
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
}
