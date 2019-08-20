package com.android.wcf.home.challenge;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.R;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Participant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

public class TeamDetailsAdapter extends RecyclerView.Adapter<TeamDetailsAdapter.TeamDetailsViewHolder> implements TeamDetailsAdapterMvp.View {

    private static final String TAG = TeamDetailsAdapter.class.getSimpleName();

    TeamDetailsAdapterMvp.Presenter teamDetailsAdapterPresenter;
    int teamSizeLimit;
    String myParticipantId;
    boolean challengeStarted = false;
    DecimalFormat decimalFormatter = new DecimalFormat("##,###.##");


    public TeamDetailsAdapter(TeamDetailsAdapterMvp.Host host, int teamSizeLimit, String participantId, boolean challengeStarted) {
        super();
        this.teamSizeLimit = teamSizeLimit;
        this.myParticipantId = participantId;
        this.challengeStarted = challengeStarted;

        teamDetailsAdapterPresenter = new TeamDetailsAdapterPresenter(this, host);
    }

    @Override
    public TeamDetailsAdapter.TeamDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_team_details_item, parent, false);
        return new TeamDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamDetailsAdapter.TeamDetailsViewHolder holder, int position) {
        Participant participant = teamDetailsAdapterPresenter.getParticipant(position);
        Resources res = holder.itemView.getContext().getResources();

        holder.participantRank.setText(position + 1 + "");

        String profileImageUrl = participant.getParticipantProfile();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Log.d(TAG, "profileImageUrl=" + profileImageUrl);

            Glide.with(holder.itemView.getContext())
                    .load(profileImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.participantImage);
        }

        holder.participantName.setText(participant.getName());
        if (myParticipantId.equals(participant.getParticipantId())) {
            holder.participantName.setTextColor(res.getColor(R.color.color_primary));
        }
        else {
            holder.participantName.setTextColor(res.getColor(android.R.color.tab_indicator_text));
        }

        holder.participantDistanceCommitment.setText(participant.getCommitmentMiles() + " mi");
        holder.participantFundsCommitted.setText("$" + decimalFormatter.format(participant.getFundsCommitted()));

        if (challengeStarted) {
            double milesWalked = participant.getCompletedSteps() / (Constants.STEPS_IN_A_MILE * 1.0);
            holder.participantDistanceWalked.setText(decimalFormatter.format(milesWalked));
            holder.participantFundsAccrued.setText(decimalFormatter.format(participant.getFundsAccrued()));
        }

        holder.participantDistanceSepartor.setVisibility(challengeStarted ? View.VISIBLE : View.GONE);
        holder.participantDistanceWalked.setVisibility(challengeStarted ? View.VISIBLE : View.GONE);
        holder.participantFundsSepartor.setVisibility(challengeStarted ? View.VISIBLE : View.GONE);
        holder.participantFundsAccrued.setVisibility(challengeStarted ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateParticipantsData(List<Participant> participantList) {
        teamDetailsAdapterPresenter.updateParticipantsData(participantList);
    }

    @Override
    public void clearSelectionPosition() {

    }

    @Override
    public int getItemCount() {
        return teamDetailsAdapterPresenter.getParticipantsCount() ;
    }

    @Override
    public void participantsDataUpdated() {
        notifyDataSetChanged();
    }

    static class TeamDetailsViewHolder extends RecyclerView.ViewHolder {
        TextView participantRank;
        ImageView participantImage;
        TextView participantName;
        TextView participantDistanceCommitment;
        TextView participantDistanceWalked;
        TextView participantFundsCommitted;
        TextView participantDistanceSepartor;
        TextView participantFundsAccrued;
        TextView participantFundsSepartor;

        public TeamDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            setupView(itemView);
        }

        private void setupView(View view) {
            participantRank = view.findViewById(R.id.participant_rank);
            participantImage = view.findViewById(R.id.participant_image);
            participantName = view.findViewById(R.id.participant_name);
            participantDistanceSepartor = view.findViewById(R.id.participant_distance_separtor);
            participantDistanceCommitment = view.findViewById(R.id.participant_distance_commitment);
            participantDistanceWalked = view.findViewById(R.id.participant_distance_walked);

            participantFundsSepartor = view.findViewById(R.id.participant_funds_separtor);
            participantFundsCommitted = view.findViewById(R.id.participant_funds_committed);
            participantFundsAccrued = view.findViewById(R.id.participant_funds_accrued);
        }
    }
}
