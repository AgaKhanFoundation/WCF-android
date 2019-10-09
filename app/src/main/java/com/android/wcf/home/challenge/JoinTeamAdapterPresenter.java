package com.android.wcf.home.challenge;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.wcf.model.Team;

import java.util.ArrayList;
import java.util.List;

public class JoinTeamAdapterPresenter implements JoinTeamAdapterMvp.Presenter {

    private static final String TAG = JoinTeamAdapterPresenter.class.getSimpleName();

    JoinTeamAdapterMvp.View view;
    List<Team> teams = new ArrayList<>();
    List<Team> teamsOrig = new ArrayList<>();

    JoinTeamAdapterMvp.Host host;

    int selectedTeamPosition = -1;

    public JoinTeamAdapterPresenter(@NonNull JoinTeamAdapterMvp.View view, @NonNull JoinTeamAdapterMvp.Host host) {
        this.view = view;
        this.host = host;
    }

    @Override
    public void updateTeamsData(List<Team> teams) {
        this.teamsOrig.clear();
        this.teamsOrig.addAll(teams);
        this.teams.clear();
        this.teams.addAll(teams);
        view.teamsDataUpdated();
    }

    @Override
    public void updateFilterTeamsData(List<Team> teams) {
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
    public List<Team> filterTeams(String filterValue) {
        if (filterValue.isEmpty()) {
           return teamsOrig;
        }
        List<Team> filteredList = new ArrayList<>();
        for (Team team : teamsOrig) {
            if (team.getName().toLowerCase().contains(filterValue)) {
                filteredList.add(team);
            }
        }
        return filteredList;
    }

    @Override
    public void onTeamSelected(int pos) {
        Log.d(TAG, "onTeamSelected(pos)=" + pos);
        int prevSelectedPos = selectedTeamPosition;
        for (int idx = 0; idx < teams.size(); idx++) {
            teams.get(idx).setSelectedToJoin(pos == idx);
        }

        if (prevSelectedPos >= 0) {
            Team team = getTeam(prevSelectedPos);
            if (team != null) {
                view.teamRowUnselected(prevSelectedPos);
            }
        }
        Team team = getTeam(pos);
        if (team != null) {
            view.teamRowSelected(pos);
        }

        host.removeFocusFromSearch();
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
        if (teams != null && selectedTeamPosition >= 0 && selectedTeamPosition < teams.size() ) {
            teams.get(selectedTeamPosition).setSelectedToJoin(false);
            view.teamRowUnselected(selectedTeamPosition);
        }
        selectedTeamPosition = -1;
    }

    @Override
    public void teamRowSelected(int pos) {
        selectedTeamPosition = pos;
        host.teamRowSelected(pos);

    }

    @Override
    public boolean isTeamSelectable(int pos, int teamSizeLimit) {
        if (pos < 0 || teams == null || pos >= teams.size()) {
            return false;
        }

        Team team = teams.get(pos);
        int participantsCount = team.getParticipants() != null ? team.getParticipants().size() : 0;

        int spotAvailable = teamSizeLimit - participantsCount;
        return teams.get(pos).getVisibility() && spotAvailable > 0;
    }
}
