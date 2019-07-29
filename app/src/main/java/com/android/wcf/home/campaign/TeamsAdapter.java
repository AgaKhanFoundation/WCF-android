package com.android.wcf.home.campaign;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.wcf.R;
import com.android.wcf.model.Team;

import java.util.List;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.TeamViewHolder> implements TeamsAdapterMvp.View {

    TeamsAdapterMvp.Presenter teamsAdapterPresenter;

    public TeamsAdapter(TeamsAdapterMvp.Host host) {
        super();
        teamsAdapterPresenter = new TeamsAdapterPresenter(this, host);
    }

    @Override
    public void clearTeamSelectionPosition() {
        teamsAdapterPresenter.clearTeamSelectionPosition();
    }

    @Override
    public void updateTeamsData(List<Team> teams) {
        teamsAdapterPresenter.updateTeamsData(teams);
    }

    @Override
    public Team getSelectedTeam() {
      return teamsAdapterPresenter.getSelectedTeam();
    }

    @Override
    public int getItemCount() {
        return teamsAdapterPresenter.getTeamsCount();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_team_item, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder teamViewHolder, final int pos) {
        Team team = teamsAdapterPresenter.getTeam(pos);

        teamViewHolder.teamName.setText(team.getName());
        teamViewHolder.teamLeadName.setText(team.getLeaderName());

        int participants = team.getParticipants() != null ? team.getParticipants().size() : 0;
        String members = participants == 0 ? "No members yet" : participants + " members";
        teamViewHolder.memberCount.setText(members);

        int unpledged = 99; //TODO: how to compute this? perhaps a helper method on Team object?
        String distanceUnits = "miles";
        String distanceUnpledged = String.format("%d %s unpledged", unpledged, distanceUnits);
        teamViewHolder.unpledged.setText(distanceUnpledged);

        teamViewHolder.itemView.setSelected(teamsAdapterPresenter.getSelectedTeamPosition() == pos);

        teamViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamsAdapterPresenter.onTeamSelected(pos);
            }
        });
    }

    @Override
    public void teamsDataUpdated() {
        notifyDataSetChanged();
    }

    @Override
    public void teamRowSelected(int pos) {
        notifyDataSetChanged(); //TODO: optimize to repaint only the visible rows?
        teamsAdapterPresenter.teamRowSelected(pos);
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamName;
        TextView teamLeadName;
        TextView memberCount;
        TextView unpledged;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            setupView(itemView);
        }

        private void setupView(View view) {
            teamName = view.findViewById(R.id.team_name);
            teamLeadName = view.findViewById(R.id.team_lead_name);
            memberCount = view.findViewById(R.id.team_member_count);
            unpledged = view.findViewById(R.id.team_distance_unpledged_count);
        }
    }
}
