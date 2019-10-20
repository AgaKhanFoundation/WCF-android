package com.android.wcf.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wcf.R;
import com.android.wcf.application.DataHolder;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.helper.view.ListPaddingDecoration;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;
import com.android.wcf.model.Participant;
import com.android.wcf.model.Team;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import static com.android.wcf.application.WCFApplication.isProdBackend;

public class TeamMembershipFragment extends BaseFragment implements TeamMembershipMvp.View, TeamMembershipAdapterMvp.Host {

    private static final String TAG = TeamMembershipFragment.class.getSimpleName();
    private static final String IS_TEAM_LEAD_ARG = "is_team_lead";

    private TeamMembershipMvp.Host host;
    private Team team;
    private Event event;
    private boolean isTeamLead = false;

    private TeamMembershipMvp.Presenter presenter;
    private RecyclerView teamMembershipRecyclerView = null;
    private TeamMembershipAdapter teamMembershipAdapter = null;
    private View settingsTeamProfileContainer;
    private View settingsTeamMembershipContainer;
    private View settingsTeamInviteContainer;
    private View deleteTeamContainer;

    private MenuItem teamEditMenuEtem;
    private boolean inEditMode = false;

    AlertDialog editTeamNameDialogBuilder;
    View editTeamDialogView = null;

    TextView teamNameTv;

    EditTextDialogListener editTeamNameDialogListener = new EditTextDialogListener() {
        @Override
        public void onDialogDone(@NotNull String newName) {
            TextView editTeamteamNameTv = teamNameTv;
            editTeamteamNameTv.setText(newName);
            DataHolder.updateParticipantTeamName(newName);
        }

        @Override
        public void onDialogCancel() {
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.team_invite_chevron:
                    inviteTeamMembers();
                    break;
                case R.id.delete_team:
                    confirmDeleteTeam();
                case R.id.team_name:
                    editTeamName();
            }
        }
    };

    public static TeamMembershipFragment getInstance(boolean isTeamLead) {
        TeamMembershipFragment fragment = new TeamMembershipFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_TEAM_LEAD_ARG, isTeamLead);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TeamMembershipMvp.Host) {
            host = (TeamMembershipMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TeamChallengeProgressMvp.Host");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        editTeamNameDialogBuilder = new AlertDialog.Builder(getContext()).create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            isTeamLead = bundle.getBoolean(IS_TEAM_LEAD_ARG);
        }

        View fragmentView = inflater.inflate(R.layout.fragment_team_membership, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        team = getParticipantTeam();
        event = getEvent();
        host.setToolbarTitle(getString(R.string.settings_team_membership_title), true);
        presenter = new TeamMembershipPresenter(this);
        setupView(view);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_team_edit, menu);
        teamEditMenuEtem = menu.findItem(R.id.menu_item_team_edit);
        if (team != null) {
            if (teamEditMenuEtem != null) teamEditMenuEtem.setVisible(isTeamLead);
        }
        super.onCreateOptionsMenu(menu, menuInflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (!handled) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    closeView();
                    handled = true;
                    break;
                case R.id.menu_item_team_edit:
                    inEditMode = !inEditMode;
                    teamEditMenuEtem.setTitle(inEditMode ?
                            getString(R.string.team_edit_done_title) : getString(R.string.team_edit_start_title));
                    teamMembershipAdapter.updateEditMode(inEditMode);
                default:
                    break;
            }
        }
        return handled;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshTeamParticipantsList();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
        editTeamNameDialogBuilder = null;
    }

    void refreshTeamParticipantsList() {
        teamMembershipAdapter.clearSelectionPosition();
        teamMembershipRecyclerView.scrollToPosition(0);
        teamMembershipAdapter.updateParticipantsData(team.getParticipants());
    }

    void setupView(View fragmentView) {

        settingsTeamProfileContainer = fragmentView.findViewById(R.id.settings_team_profile_container);
        settingsTeamMembershipContainer = fragmentView.findViewById(R.id.settings_team_membership_container);
        settingsTeamInviteContainer = fragmentView.findViewById(R.id.settings_team_invite_container);
        deleteTeamContainer = fragmentView.findViewById(R.id.settings_delete_team_container);

        setupSettingsTeamProfileContainer(settingsTeamProfileContainer);
        setupSettingsTeamMembershipContainer(settingsTeamMembershipContainer);
        setupChallengeTeamInviteContainer(settingsTeamInviteContainer);
        setupDeleteTeamContainer(deleteTeamContainer);

    }

    void setupSettingsTeamProfileContainer(View container) {
        ImageView teamProfileImage = container.findViewById(R.id.team_image);
        teamNameTv = container.findViewById(R.id.team_name);

        TextView challengeNameTv = container.findViewById(R.id.challenge_name);
        TextView challengeDatesTv = container.findViewById(R.id.challenge_dates);

        Event event = getEvent();
        challengeNameTv.setText(event.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
        String startDate = sdf.format(event.getStartDate());
        String endDate = sdf.format(event.getEndDate());
        challengeDatesTv.setText(startDate + " to " + endDate);

        //TODO: remove this when new date for challenge is decided
        if (isProdBackend() && Constants.getChallengeStartSoonMessage()) {
            challengeDatesTv.setText(getString(R.string.message_journey_starting_soon));
        }

        Team team = getParticipantTeam();
        teamNameTv.setText(team.getName());
        if (isTeamLead) {
            teamNameTv.setOnClickListener(onClickListener);
        } else {
            teamNameTv.setOnClickListener(null);
        }

        String teamImageUrl = team.getImage();
        if (teamImageUrl != null && !teamImageUrl.isEmpty()) {
            Log.d(TAG, "teamImageUrl=" + teamImageUrl);

            Glide.with(getContext())
                    .load(teamImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(teamProfileImage);
        }
    }

    void setupSettingsTeamMembershipContainer(View container) {
        if (teamMembershipAdapter == null) {
            String teamLeadParticipantId = "";
            if (team != null) {
                isTeamLead = team.isTeamLeader(SharedPreferencesUtil.getMyParticipantId());
                teamLeadParticipantId = team.getLeaderId();
            }

            teamMembershipAdapter = new TeamMembershipAdapter(this,
                    event.getTeamLimit(),
                    SharedPreferencesUtil.getMyParticipantId(),
                    teamLeadParticipantId,
                    isTeamLead,
                    event.hasChallengeStarted()
            );
        }

        teamMembershipRecyclerView = container.findViewById(R.id.team_members_list);
        teamMembershipRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        teamMembershipRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        teamMembershipRecyclerView.addItemDecoration(new ListPaddingDecoration(getContext()));

        teamMembershipRecyclerView.setAdapter(teamMembershipAdapter);

    }

    void setupChallengeTeamInviteContainer(View container) {
        TextView inviteLabel = container.findViewById(R.id.team_invite_label);
        boolean showTeamInvite = false;
        if (isTeamLead && event != null) {
            if (event.daysToStartEvent() >= 0 && !event.hasTeamBuildingEnded()) {
                List<Participant> participants = team.getParticipants();
                if (participants != null) {
                    int openSlots = event.getTeamLimit() - participants.size();
                    if (openSlots > 0) {
                        showTeamInvite = true;
                        String openSlotMessage = getResources().getQuantityString(R.plurals.team_invite_more_members_message, openSlots, openSlots);
                        inviteLabel.setText(openSlotMessage);
                    }
                }
            }
        }
        if (showTeamInvite) {
            View image = container.findViewById(R.id.team_invite_chevron);
            expandViewHitArea(image, container);
            image.setOnClickListener(onClickListener);
        }
        container.setVisibility(showTeamInvite ? View.VISIBLE : View.GONE);
    }

    void setupDeleteTeamContainer(View container) {
        if (isTeamLead) {
            container.setVisibility(View.VISIBLE);
            container.findViewById(R.id.delete_team).setOnClickListener(onClickListener);
        } else {
            container.setVisibility(View.GONE);
            container.findViewById(R.id.delete_team).setOnClickListener(null);
        }
    }

    void closeView() {
        getActivity().onBackPressed();
    }

    @Override
    public void removeMemberFromTeam(String participantName, String participantId) {
        confirmToRemoveTeamMember(participantName, participantId);
    }

    @Override
    public void participantRemovedFromTeam(String participantId) {
        Iterator participantIterator = team.getParticipants().iterator();
        while (participantIterator.hasNext()) {
            Participant participant = (Participant) participantIterator.next();
            if (participant.getParticipantId().equals(participantId)) {
                participantIterator.remove();
                refreshTeamParticipantsList();
                setupChallengeTeamInviteContainer(settingsTeamInviteContainer);
                break;
            }
        }
    }

    public void confirmToRemoveTeamMember(String participantName, final String participantId) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_remove_team_member, null);

        TextView title = dialogView.findViewById(R.id.remove_team_member_title);
        title.setText(getString(R.string.remove_team_member_title, participantName));

        TextView message = dialogView.findViewById(R.id.remove_team_member_message);
        message.setText(getString(R.string.remove_team_member_message, participantName));

        Button removeBtn = dialogView.findViewById(R.id.remove_team_member_button);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_remove_team_member_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.removeMemberFromTeam(participantId);
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    void confirmDeleteTeam() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_confirm_delete_team, null);

        Button deleteBtn = dialogView.findViewById(R.id.delete_team_button);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_delete_team_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.deleteTeam(team.getId());
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    @Override
    public void onTeamDeleteSuccess() {
        SharedPreferencesUtil.clearMyTeamId();
        clearCacheTeamList();
        clearCachedParticipantTeam();
        clearCachedParticipant();
        host.restartHomeActivity();
    }

    @Override
    public void onTeamDeleteError(Throwable error) {
        showError("Team Delete Error", error.getMessage(), null);
    }

    public void editTeamName() {
        final TextView teamNameTV = settingsTeamProfileContainer.findViewById(R.id.team_name);
        final String currentTeamName = teamNameTV.getText().toString();
        presenter.onEditTeamName(presenter, currentTeamName, editTeamNameDialogListener);
    }

    @Override
    public void showTeamNameEditDialog(final TeamMembershipMvp.Presenter presenter,
                                       final String currentName,
                                       final EditTextDialogListener editTextDialogListener) {

        LayoutInflater inflater = this.getLayoutInflater();
        editTeamDialogView = inflater.inflate(R.layout.view_team_name_edit, null);

        final Button saveBtn = editTeamDialogView.findViewById(R.id.save);
        final Button cancelBtn = editTeamDialogView.findViewById(R.id.cancel);

        final TextInputLayout teamNameInputLayout = editTeamDialogView.findViewById(R.id.team_name_input_layout);
        final TextInputEditText teamNameEditText = editTeamDialogView.findViewById(R.id.team_name);

        TextWatcher editTeamNameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                boolean enabled = false;
                String teamName = teamNameEditText.getText().toString();
                if (teamName.trim().length() >= Constants.MIN_TEAM_NAME_CHAR_LENGTH) {

                    if (!teamName.equals(currentName)) {
                        enabled = true;
                    }
                }
                saveBtn.setEnabled(enabled);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        teamNameEditText.setText(currentName);
        teamNameEditText.addTextChangedListener(editTeamNameWatcher);

        //disable the saveBtn initially. It will be enabled when team name entered
        boolean enabled = false;
        String teamName = teamNameEditText.getText().toString();
        if (teamName.trim().length() >= Constants.MIN_TEAM_NAME_CHAR_LENGTH) {

            if (!teamName.equals(currentName)) {
                enabled = true;
            }
        }
        saveBtn.setEnabled(enabled);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTeamNameDialogBuilder.dismiss();
                editTeamNameDialogBuilder.setView(null);

                if (editTextDialogListener != null) {
                    editTextDialogListener.onDialogCancel();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = teamNameEditText.getText().toString();
                if (!newName.isEmpty() && !newName.equals(currentName)) {
                    presenter.updateTeamName(team.getId(), newName);
                }
            }
        });

        editTeamNameDialogBuilder.setView(editTeamDialogView);
        editTeamNameDialogBuilder.show();
    }

    @Override
    public void onTeamNameUpdateSuccess(String teamName) {
        editTeamNameDialogBuilder.dismiss();
        if (editTeamNameDialogListener != null) {
            editTeamNameDialogListener.onDialogDone(teamName);
        }
    }

    @Override
    public void onTeamNameUpdateConstraintError(String teamName) {
        if (editTeamDialogView != null) {
            TextInputLayout teamNameInputLayout = editTeamDialogView.findViewById(R.id.team_name_input_layout);

            if (teamNameInputLayout != null) {
                teamNameInputLayout.setError(getString(R.string.duplicate_team_name_error));
            }
        }
    }

    @Override
    public void onTeamNameUpdateError(@NotNull Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.events_data_error);
        }
        else {
            TextInputLayout teamNameInputLayout = editTeamDialogView.findViewById(R.id.team_name_input_layout);
            if (teamNameInputLayout != null) {
                teamNameInputLayout.setError(error.getMessage());
            }
        }
    }
}