package com.android.wcf.home.Leaderboard;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.wcf.R;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.TeamViewHolder>
        implements LeaderboardAdapterMvp.View {

    LeaderboardAdapterMvp.Host mHost;
    LeaderboardAdapterMvp.Presenter mPresenter;

    public LeaderboardAdapter(LeaderboardAdapterMvp.Host host) {
        this.mHost = mHost;
        mPresenter = new LeaderboardAdapterPresenter(this);
    }

    public LeaderboardAdapterMvp.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void leaderboardDataUpdated() {

    }

    @Override
    public int getItemCount() {
        return mPresenter.getTeamsCount();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_leaderboard_team_item, parent,false);

        return new  TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.TeamViewHolder teamViewHolder, int pos) {
        LeaderboardTeam team = mPresenter.getTeam(pos);

        teamViewHolder.teamRank.setText(team.getRank() + "");
        teamViewHolder.teamName.setText(team.getName());
        teamViewHolder.distanceCompleted.setText(String.format("%,6d", team.getDistanceCompleted()));
        teamViewHolder.amountRaised.setText(String.format("$%,.02f", team.getAmountAccrued()));
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
