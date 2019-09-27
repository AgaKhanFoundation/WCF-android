package com.android.wcf.home.challenge;

import androidx.annotation.NonNull;

import com.android.wcf.model.Team;

import java.util.ArrayList;
import java.util.List;

public class JoinTeamAdapterPresenter implements JoinTeamAdapterMvp.Presenter {

    JoinTeamAdapterMvp.View view;
    List<Team> teams = new ArrayList<>();
    JoinTeamAdapterMvp.Host host;

    int selectedTeamPosition = -1;

    public JoinTeamAdapterPresenter(@NonNull JoinTeamAdapterMvp.View view, @NonNull JoinTeamAdapterMvp.Host host) {
        this.view = view;
        this.host = host;
    }

    @Override
    public void updateTeamsData(List<Team> teams) {
        this.teams.clear();
        this.teams.addAll(teams);
        view.teamsDataUpdated();
    }

    @Override
    public Team getTeam(int pos) {
        if (teams != null && pos < teams.size() && pos >= 0) {
            return teams.get(pos);
        }
        return null;
    }

    @Override
    public int getTeamsCount() {
        return teams != null ? teams.size() : 0;
    }

    @Override
    public void onTeamSelected(int pos) {
        selectedTeamPosition = pos;
        view.teamRowSelected(pos);
    }

    @Override
    public Team getSelectedTeam() {
        return selectedTeamPosition != -1 ? getTeam(selectedTeamPosition) : null;
    }

    @Override
    public int getSelectedTeamPosition() {
        return selectedTeamPosition;
    }

    @Override
    public void clearTeamSelectionPosition() {
        selectedTeamPosition = -1;
    }

    @Override
    public void teamRowSelected(int pos) {
        host.teamRowSelected(pos);

    }

    @Override
    public boolean isTeamSelectable(int pos) {
        if (pos < 0 || pos >= teams.size()) {
            return false;
        }
        return teams.get(pos).getVisibility();
    }
}
