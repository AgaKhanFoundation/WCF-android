package com.android.wcf.settings;

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
import com.android.wcf.model.Participant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.List;

public class TeamMembershipAdapter extends RecyclerView.Adapter<TeamMembershipAdapter.TeamMembershipViewHolder> implements TeamMembershipAdapterMvp.View {

    private static final String TAG = TeamMembershipAdapter.class.getSimpleName();

    TeamMembershipAdapterMvp.Presenter teamMembershipAdapterPresenter;
    int teamSizeLimit;
    String myParticipantId;
    String teamLeadParticipantId;
    boolean challengeStarted = false;
    boolean teamLead = false;
    boolean inEditMode = false;

    public TeamMembershipAdapter(TeamMembershipAdapterMvp.Host host, int teamSizeLimit, String participantId,
                                 String teamLeadParticipantId, boolean teamLead, boolean challengeStarted) {
        super();
        this.teamSizeLimit = teamSizeLimit;
        this.myParticipantId = participantId;
        this.teamLeadParticipantId = teamLeadParticipantId;
        this.teamLead = teamLead;
        this.challengeStarted = challengeStarted;

        teamMembershipAdapterPresenter = new TeamMembershipAdapterPresenter(this, host);
    }

    @Override
    public TeamMembershipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_team_details_membership_item, parent, false);
        return new TeamMembershipViewHolder(view, teamMembershipAdapterPresenter);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamMembershipViewHolder holder, int position) {

        Participant participant = teamMembershipAdapterPresenter.getParticipant(position);
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
        } else {
            holder.participantName.setTextColor(res.getColor(android.R.color.tab_indicator_text));
        }

        if (teamLeadParticipantId.equals(participant.getParticipantId())) {
            holder.removeMemberTv.setVisibility(View.GONE);
            holder.teamLeadTv.setVisibility(View.VISIBLE);
            holder.removeMemberTv.setOnClickListener(null);
        }
        else {
            holder.removeMemberTv.setVisibility((teamLead && inEditMode) ? View.VISIBLE : View.GONE);
            holder.teamLeadTv.setVisibility(View.GONE);
            holder.removeMemberTv.setOnClickListener((teamLead && inEditMode) ? holder : null);
        }
    }

    @Override
    public void updateParticipantsData(List<Participant> participantList) {
        teamMembershipAdapterPresenter.updateParticipantsData(participantList);
    }

    @Override
    public void updateEditMode(boolean newMode) {
        teamMembershipAdapterPresenter.updateEditMode(newMode);
    }

    public void editModeUpdated(boolean editMode) {
        inEditMode = editMode;
        notifyDataSetChanged();
    }

    @Override
    public void clearSelectionPosition() {

    }

    @Override
    public int getItemCount() {
        return teamMembershipAdapterPresenter.getParticipantsCount() ;
    }

    @Override
    public void participantsDataUpdated() {
        notifyDataSetChanged();
    }

    static class TeamMembershipViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView participantRank;
        ImageView participantImage;
        TextView participantName;
        TextView removeMemberTv;
        TextView teamLeadTv;

        TeamMembershipAdapterMvp.Presenter presenter;

        public TeamMembershipViewHolder(@NonNull View itemView, TeamMembershipAdapterMvp.Presenter presenter) {
            super(itemView);
            this.presenter = presenter;
            setupView(itemView);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.team_member_remove:
                    int pos = getAdapterPosition();
                    presenter.removeMemberFromTeam(pos);
                    break;
            }

        }

        private void setupView(View view) {
            participantRank = view.findViewById(R.id.participant_rank);
            participantImage = view.findViewById(R.id.participant_image);
            participantName = view.findViewById(R.id.participant_name);
            removeMemberTv = view.findViewById(R.id.team_member_remove);
            teamLeadTv = view.findViewById(R.id.team_lead_label);
        }
    }
}
