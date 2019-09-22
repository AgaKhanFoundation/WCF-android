package com.android.wcf.home.challenge;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

public class CreateTeamFragment extends BaseFragment implements CreateTeamMvp.View {

    private static final String TAG = CreateTeamFragment.class.getSimpleName();
    public static final int MIN_TEAM_NAME_SIZE = 6;

    CreateTeamPresenter presenter;
    CreateTeamMvp.Host host;
    private View createTeamCard = null;
    private View teamCreatedCard = null;
    private Button createTeamButton = null;
    private Button cancelCreateTeamButton = null;
    private TextInputLayout teamNameInputLayout = null;
    private TextInputEditText teamNameEditText = null;
    private SwitchCompat teamVisibiltySwitch = null;

    private TextView teamInviteMessageTv = null;
    private Button inviteMembersButton = null;
    private View teamCreatedContinueView = null;

    private boolean teamCreated = false;

    private TextWatcher creatTeamEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            teamNameInputLayout.setError(null);
            enableCreateTeamButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.create_team_button:
                    closeKeyboard();
                    String teamName = teamNameEditText.getText().toString();
                    String teamLeadParticipantId = SharedPreferencesUtil.getMyParticipantId();
                    boolean teamVisibility = teamVisibiltySwitch.isChecked();
                    presenter.createTeam(teamName, teamLeadParticipantId, teamVisibility);
                    break;
                case R.id.cancel_create_team_button:
                    if (teamCreated) {
                        closeView();
                    } else {
                        confirmCancelTeamCreation();
                    }
                    break;
                case R.id.btn_invite_members:
                    inviteTeamMembers();
                    break;
                case R.id.team_created_continue:
                    closeView();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateTeamMvp.Host) {
            host = (CreateTeamMvp.Host) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CreateTeamMvp.Host");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CreateTeamPresenter(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_create_team, container, false);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        host.setToolbarTitle(getString(R.string.create_team_title), true);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmCancelTeamCreation();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void teamCreated(@NotNull Team team) {
        setParticipantTeam(team);
        showMessage("New team " + team.getName() + " created");
        presenter.assignParticipantToTeam(SharedPreferencesUtil.getMyParticipantId(), team.getId());
    }

    @Override
    public void participantJoinedTeam(@NotNull String participantId, int teamId) {
        SharedPreferencesUtil.saveMyTeamId(teamId);
        teamCreated = true;
        showTeamCreatedView();
    }

    @Override
    public void showCreateTeamConstraintError() {
        if (teamNameInputLayout != null) {
            teamNameInputLayout.setError(getString(R.string.duplicate_team_name_error));
        }
    }

    @Override
    public void confirmCancelTeamCreation() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_confirm_cancel_team_creation, null);

        Button leaveBtn = dialogView.findViewById(R.id.ok_button);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                closeView();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    void closeView() {
        getActivity().onBackPressed();
    }

    void setupView(View fragmentView) {

        createTeamCard = fragmentView.findViewById(R.id.create_team_card);
        teamCreatedCard = fragmentView.findViewById(R.id.team_created_card);

        setupCreateTeamCardView(createTeamCard);
        setupTeamCreatedCardView(teamCreatedCard);
        showCreateTeamView();
    }

    void setupCreateTeamCardView(View createTeamCard) {

        createTeamButton = createTeamCard.findViewById(R.id.create_team_button);
        if (createTeamButton != null) {
            createTeamButton.setOnClickListener(onClickListener);
        }

        cancelCreateTeamButton = createTeamCard.findViewById(R.id.cancel_create_team_button);
        if (cancelCreateTeamButton != null) {
            cancelCreateTeamButton.setOnClickListener(onClickListener);
        }

        teamNameEditText = createTeamCard.findViewById(R.id.team_name);
        if (teamNameEditText != null) {
            teamNameEditText.addTextChangedListener(creatTeamEditWatcher);
        }
        teamNameInputLayout = createTeamCard.findViewById(R.id.team_name_input_layout);

        teamVisibiltySwitch = createTeamCard.findViewById(R.id.team_public_visibility_enabled);

        enableCreateTeamButton();

    }

    void setupTeamCreatedCardView(View view) {
        teamInviteMessageTv = view.findViewById(R.id.team_invite_message);
        Event event = getEvent();
        teamInviteMessageTv.setText(getString(R.string.team_created_invite_members_message, event.getTeamLimit() - 1));
        inviteMembersButton = view.findViewById(R.id.btn_invite_members);
        teamCreatedContinueView = view.findViewById(R.id.team_created_continue);
        inviteMembersButton.setOnClickListener(onClickListener);
        teamCreatedContinueView.setOnClickListener(onClickListener);
    }

    void showCreateTeamView() {
        createTeamCard.setVisibility(View.VISIBLE);
        teamCreatedCard.setVisibility(View.GONE);
    }

    void showTeamCreatedView() {
        createTeamCard.setVisibility(View.GONE);
        teamCreatedCard.setVisibility(View.VISIBLE);
    }

    void enableCreateTeamButton() {
        boolean enabled = false;
        if (teamNameEditText != null) {
            String teamName = teamNameEditText.getText().toString();
            if (teamName.trim().length() >= MIN_TEAM_NAME_SIZE) {
                enabled = true;
            }
        }
        createTeamButton.setEnabled(enabled);
    }

}
