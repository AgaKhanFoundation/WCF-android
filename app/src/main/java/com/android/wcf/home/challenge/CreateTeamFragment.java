package com.android.wcf.home.challenge;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Team;

import org.jetbrains.annotations.NotNull;

public class CreateTeamFragment extends BaseFragment implements CreateTeamMvp.View {

    private static final String TAG = CreateTeamFragment.class.getSimpleName();
    public static final int MIN_TEAM_NAME_SIZE = 6;

    CreateTeamPresenter presenter;
    CreateTeamMvp.Host host;
    private Button createTeamButton = null;
    private Button cancelCreateTeamButton = null;
    private EditText teamNameEditText = null;
    private SwitchCompat teamVisibiltySwitch = null;

    private TextWatcher creatTeamEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
                    boolean teamVisibility =  teamVisibiltySwitch.isChecked();
                    presenter.createTeam(teamName, teamLeadParticipantId, teamVisibility);
                    break;
                case R.id.cancel_create_team_button:
                    closeKeyboard();
                    closeView();
                    break;
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
        setupView(getView());
        host.setToolbarTitle(getString(R.string.create_team_title));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
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
        closeView();
    }

    void closeView() {
        getActivity().onBackPressed();
    }

    void setupView(View fragmentView) {

        createTeamButton = fragmentView.findViewById(R.id.create_team_button);
        if (createTeamButton != null) {
            createTeamButton.setOnClickListener(onClickListener);
        }

        cancelCreateTeamButton = fragmentView.findViewById(R.id.cancel_create_team_button);
        if (cancelCreateTeamButton != null) {
            cancelCreateTeamButton.setOnClickListener(onClickListener);
        }

        teamNameEditText = fragmentView.findViewById(R.id.team_name);
        if (teamNameEditText != null) {
            teamNameEditText.addTextChangedListener(creatTeamEditWatcher);
        }
        teamVisibiltySwitch = fragmentView.findViewById(R.id.team_public_visibility_enabled);

        enableCreateTeamButton();

    }

    void enableCreateTeamButton() {
        boolean enabled = false;
        if (teamNameEditText != null ) {
            String teamName = teamNameEditText.getText().toString();
            if (teamName.trim().length() >= MIN_TEAM_NAME_SIZE) {
                enabled = true;
            }
        }
        createTeamButton.setEnabled(enabled);
    }

}
