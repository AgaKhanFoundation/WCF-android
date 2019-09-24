package com.android.wcf.home.challenge;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.R;
import com.android.wcf.model.Team;

import java.util.List;

public class JoinTeamAdapter extends RecyclerView.Adapter<JoinTeamAdapter.TeamViewHolder> implements JoinTeamAdapterMvp.View {

    JoinTeamAdapterMvp.Presenter teamsAdapterPresenter;
    int teamSizeLimit;


    public JoinTeamAdapter(JoinTeamAdapterMvp.Host host, int teamSizeLimit) {
        super();
        this.teamSizeLimit = teamSizeLimit;
        teamsAdapterPresenter = new JoinTeamAdapterPresenter(this, host);
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
                .inflate(R.layout.view_list_join_team_item, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder teamViewHolder, final int pos) {
        Team team = teamsAdapterPresenter.getTeam(pos);

        teamViewHolder.teamName.setText(team.getName());
        String teamLeadName = team.getLeaderName();
        if (teamLeadName != null && !teamLeadName.isEmpty()) {
            teamViewHolder.teamLeadName.setText(team.getLeaderName());
            teamViewHolder.teamLeadName.setVisibility(View.VISIBLE);
        }
        else {
            teamViewHolder.teamLeadName.setVisibility(View.GONE);
        }

        int participants = team.getParticipants() != null ? team.getParticipants().size() : 0;

        Resources res = teamViewHolder.itemView.getContext().getResources();
        String spotsMessage = "";
        if (participants < teamSizeLimit) {
            spotsMessage = res.getString(R.string.team_spots_message_template, teamSizeLimit - participants, teamSizeLimit);
        }
        else {
            spotsMessage = res.getString(R.string.no_spots_available_message);
        }
        teamViewHolder.teamSpotsMessage.setText(spotsMessage);
        teamViewHolder.rbTeamSelected.setChecked(teamsAdapterPresenter.getSelectedTeamPosition() == pos);
        teamViewHolder.itemView.setSelected(teamsAdapterPresenter.getSelectedTeamPosition() == pos);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamsAdapterPresenter.onTeamSelected(pos);
            }
        };

        teamViewHolder.itemView.setOnClickListener(onClickListener);
        teamViewHolder.rbTeamSelected.setOnClickListener(onClickListener);
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
        TextView teamSpotsMessage;
        RadioButton rbTeamSelected;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            setupView(itemView);
        }

        private void setupView(View view) {
            teamName = view.findViewById(R.id.team_name);
            teamLeadName = view.findViewById(R.id.team_lead_name);
            teamSpotsMessage = view.findViewById(R.id.team_spots_mmessage);
            rbTeamSelected = view.findViewById(R.id.rb_team_selected);
        }
    }
}
