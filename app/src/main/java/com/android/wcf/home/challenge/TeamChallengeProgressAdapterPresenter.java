package com.android.wcf.home.challenge;

import androidx.annotation.NonNull;

import com.android.wcf.model.Participant;

import java.util.ArrayList;
import java.util.List;

public class TeamChallengeProgressAdapterPresenter implements TeamChallengeProgressAdapterMvp.Presenter {

    TeamChallengeProgressAdapterMvp.View view;
    List<Participant> participants = new ArrayList<>();
    TeamChallengeProgressAdapterMvp.Host host;

    public TeamChallengeProgressAdapterPresenter(@NonNull TeamChallengeProgressAdapterMvp.View view, @NonNull TeamChallengeProgressAdapterMvp.Host host) {
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
