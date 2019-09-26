package com.android.wcf.home.leaderboard;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.R;

import java.text.DecimalFormat;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.TeamViewHolder>
        implements LeaderboardAdapterMvp.View {

    LeaderboardAdapterMvp.Host mHost;
    LeaderboardAdapterMvp.Presenter mAdapterPresenter;

    DecimalFormat numberFormatter = new DecimalFormat("#,###,###");

    public LeaderboardAdapter(LeaderboardAdapterMvp.Host host, int myTeamId) {
        this.mHost = host;
        mAdapterPresenter = new LeaderboardAdapterPresenter(this, myTeamId);
    }

    @Override
    public void updateLeaderboardData(List<LeaderboardTeam> leaderboard) {
        if (leaderboard.size() == 0) {
            mHost.showLeaderboardIsEmpty();
            return;
        }
        mHost.hideEmptyLeaderboardView();
        mAdapterPresenter.updateLeaderboardData(leaderboard);
    }

    @Override
    public void leaderboardDataUpdated() {
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mAdapterPresenter.getTeamsCount();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_leaderboard_team_item, parent, false);

        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.TeamViewHolder teamViewHolder, int pos) {
        Resources res = teamViewHolder.itemView.getContext().getResources();
        LeaderboardTeam team = mAdapterPresenter.getTeam(pos);

        teamViewHolder.teamRank.setText(team.getRank() + "");
        teamViewHolder.teamName.setText(team.getName());
        teamViewHolder.distanceCompleted.setText(numberFormatter.format(team.getDistanceCompleted()));
        teamViewHolder.amountRaised.setText(numberFormatter.format(team.getAmountAccrued()));

        int textColor = res.getColor(android.R.color.tab_indicator_text);

        if (team.getId() == mAdapterPresenter.getMyTeamId()) {
            textColor = res.getColor(R.color.color_primary);
        }
        teamViewHolder.teamRank.setTextColor(textColor);
        teamViewHolder.teamName.setTextColor(textColor);
        teamViewHolder.distanceCompleted.setTextColor(textColor);
        teamViewHolder.amountRaised.setTextColor(textColor);

        if (team.getAmountAccrued() == 0.0) {
            teamViewHolder.amountRaised.setVisibility(View.GONE);
        }
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamRank;
        TextView teamName;
        TextView distanceCompleted;
        TextView amountRaised;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            setupView(itemView);
        }

        private void setupView(View view) {
            teamRank = view.findViewById(R.id.team_rank);
            teamName = view.findViewById(R.id.team_name);
            distanceCompleted = view.findViewById(R.id.team_distance_completed);
            amountRaised = view.findViewById(R.id.team_amount_raised);
        }
    }
}
