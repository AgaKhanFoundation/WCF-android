package com.android.wcf.home.challenge;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
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

    private static final String TAG = JoinTeamAdapter.class.getSimpleName();
    JoinTeamAdapterMvp.Presenter teamsAdapterPresenter;
    int teamSizeLimit;
    JoinTeamAdapterMvp.Host host;
    RecyclerView parentRV = null;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag(R.integer.join_team_row_num_tag);
            Log.d(TAG, "from join_team_row_num_tag clicked on pos=" + pos);

            switch (v.getId()) {
                case R.id.team_name:
                    break;
                case R.id.team_lead_name:
                    break;
                case R.id.team_spots_mmessage:
                    break;
                case R.id.rb_team_selected:
                    break;
                default:
                    teamsAdapterPresenter.onTeamSelected(pos);
            }
        }
    };

    public JoinTeamAdapter(JoinTeamAdapterMvp.Host host, int teamSizeLimit) {
        super();
        this.host = host;
        this.teamSizeLimit = teamSizeLimit;
        teamsAdapterPresenter = new JoinTeamAdapterPresenter(this, host);
    }

    @Override
    public void clearTeamSelectionPosition() {
        int currentSelectedPos = teamsAdapterPresenter.getSelectedTeamPosition();
        teamsAdapterPresenter.clearTeamSelectionPosition();
        if (currentSelectedPos >= 0) {
            notifyItemChanged(currentSelectedPos);
        }
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

    @Override
    public long getItemId(int position) {
        if (teamsAdapterPresenter.getTeamsCount() > 0) {
            return teamsAdapterPresenter.getTeam(position).getId();
        }
        return super.getItemId(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.parentRV = recyclerView;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_join_team_item, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TeamViewHolder teamViewHolder, final int pos) {
        Log.d(TAG, "onBindViewHolder pos=" + pos);
        Team team = teamsAdapterPresenter.getTeam(pos);
        bind(teamViewHolder, pos, team);

    }

    @Override
    public void onBindViewHolder(@NonNull final TeamViewHolder teamViewHolder, final int pos, @NonNull List<Object> payloads) {
        Log.d(TAG, "onBindViewHolder payloads  pos=" + pos);
        Team team = null;
        if (payloads.isEmpty()) {
            onBindViewHolder(teamViewHolder, pos);
            return;
        }

        team = (Team) payloads.get(0);
        bind(teamViewHolder, pos, team);

    }

    private void bind(final TeamViewHolder teamViewHolder, final int pos, final Team team) {
        teamViewHolder.teamName.setText(team.getName());
        String teamLeadName = team.getLeaderName();
        if (teamLeadName != null && !teamLeadName.isEmpty()) {
            teamViewHolder.teamLeadName.setText(team.getLeaderName());
            teamViewHolder.teamLeadName.setVisibility(View.VISIBLE);
        } else {
            teamViewHolder.teamLeadName.setVisibility(View.GONE);
        }

        int participantsCount = team.getParticipants() != null ? team.getParticipants().size() : 0;
        int spotAvailable = teamSizeLimit - participantsCount;

        final Context context = teamViewHolder.itemView.getContext();
        Resources res = context.getResources();
        String spotsMessage = "";

        if (spotAvailable > 0) {
            spotsMessage = res.getString(R.string.team_spots_message_template, teamSizeLimit - participantsCount, teamSizeLimit);
        } else {
            spotsMessage = res.getString(R.string.no_spots_available_message);
        }

        teamViewHolder.teamSpotsMessage.setText(spotsMessage);

        teamViewHolder.rbTeamSelected.setChecked(team.getSelectedToJoin());

        boolean teamSelectable = teamsAdapterPresenter.isTeamSelectable(pos, teamSizeLimit);

        teamViewHolder.rbTeamSelected.setEnabled(teamSelectable);
        if (teamSelectable) {
            teamViewHolder.itemView.setOnClickListener(onClickListener);
        }
        else {
            teamViewHolder.itemView.setOnClickListener(null);
        }
        teamViewHolder.itemView.setTag(R.integer.join_team_row_num_tag, pos);

        teamViewHolder.itemView.setEnabled(teamSelectable);
        teamViewHolder.teamName.setEnabled(teamSelectable);
        teamViewHolder.teamLeadName.setEnabled(teamSelectable);
        teamViewHolder.teamSpotsMessage.setEnabled(teamSelectable);

        teamViewHolder.rbTeamSelected.setEnabled(false); //preventing clicks on RB, it will be checked On/Off by click on ViewHolder
        teamViewHolder.rbTeamSelected.setTag(R.integer.join_team_row_num_tag, pos);

        teamViewHolder.rbTeamSelected.setOnClickListener(onClickListener);

    }


    @Override
    public void teamsDataUpdated() {
        notifyDataSetChanged();
    }

    @Override
    public void teamRowSelected(int position) {
        Log.d(TAG, "teamRowSelected pos=" + position);

        notifyItemChanged(position, teamsAdapterPresenter.getTeam(position));

        // TODO: recheck this after updating Desing libraries
        // These two are alternatives tried to force recyclerView refresh,
        // notifyItemChanged(position);
        // notifyDataSetChanged();

        teamsAdapterPresenter.teamRowSelected(position);

        // work around to force RecyclerView refresh, the above notifyData did not work
        if (parentRV != null) {
            parentRV.scrollBy(0, -10);
            parentRV.scrollBy(0, 10);
        }
    }

    @Override
    public void teamRowUnselected(int position) {
        Log.d(TAG, "teamRowUnselected pos=" + position);

        notifyItemChanged(position, teamsAdapterPresenter.getTeam(position));
        // notifyItemChanged(position);
        // notifyDataSetChanged();

        if (parentRV != null) {
            parentRV.scrollBy(0, -10);
            parentRV.scrollBy(0, 10);
        }
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
