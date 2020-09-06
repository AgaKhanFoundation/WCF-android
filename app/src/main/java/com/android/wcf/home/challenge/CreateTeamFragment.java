package com.android.wcf.home.challenge;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.android.wcf.R;
import com.android.wcf.base.BaseFragment;
import com.android.wcf.helper.AzureImageManager;
import com.android.wcf.helper.ManifestHelper;
import com.android.wcf.helper.SharedPreferencesUtil;
import com.android.wcf.model.Constants;
import com.android.wcf.model.Event;
import com.android.wcf.model.Team;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.android.wcf.base.RequestCodes.SELECT_TEAM_IMAGE_GALlERY_REQUEST_CODE;
import static com.android.wcf.base.RequestCodes.STORAGE_PERMISSIONS_REQUEST_CODE;

public class CreateTeamFragment extends BaseFragment implements CreateTeamMvp.View {

    private static final String TAG = CreateTeamFragment.class.getSimpleName();

    CreateTeamMvp.Presenter presenter;
    CreateTeamMvp.Host host;
    private View createTeamCard = null;
    private View teamCreatedCard = null;
    private Button createTeamButton = null;
    private Button cancelCreateTeamButton = null;
    private TextInputLayout teamNameInputLayout = null;
    private ImageView teamProfileIv = null;
    private TextInputEditText teamNameEditText = null;
    private SwitchCompat teamVisibiltySwitch = null;

    private TextView teamInviteMessageTv = null;
    private Button inviteMembersButton = null;
    private View teamCreatedContinueView = null;
    private String teamImageFilename = null;
    private boolean teamCreated = false;

    private TextWatcher creatTeamEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
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
                    String teamName = teamNameEditText.getText().toString().trim();
                    String teamLeadParticipantId = SharedPreferencesUtil.getMyParticipantId();
                    boolean teamVisibility = !teamVisibiltySwitch.isChecked();
                    presenter.createTeam(teamName, teamLeadParticipantId, teamImageFilename, teamVisibility);
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
                    break;
                case R.id.team_image:
                    editTeamImage();
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
        setupView(view);
        host.setToolbarTitle(getString(R.string.create_team_title), true);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_TEAM_IMAGE_GALlERY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            AzureImageManager azureImageManager = new AzureImageManager();
            AzureImageManager.BlobUpdateCallback blobUpdateCallback = new AzureImageManager.BlobUpdateCallback() {
                @Override
                public void onUploadSuccess(final String filename) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            updateTeamImage(filename);
                        }
                    });
                }

                @Override
                public void onUploadError(String filename, final Throwable exception) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            showError(exception);
                        }
                    });
                }
            };
            azureImageManager.UploadImage(picturePath, blobUpdateCallback);
        }
    }

    public void updateTeamImage(final String teamImageFilename) {
        this.teamImageFilename = teamImageFilename;

        Glide.with(getContext()).clear(teamProfileIv);
        String wcbImageFolderUrl = ManifestHelper.Companion.getWcbImageFolderUrl();
        String teamImageUrl = wcbImageFolderUrl + teamImageFilename;

        Glide.with(getContext())
                .load(teamImageUrl)
                .placeholder(R.drawable.avatar_team)
                .error(R.drawable.avatar_team)
                .apply(RequestOptions.circleCropTransform())
                .into(teamProfileIv);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, SELECT_TEAM_IMAGE_GALlERY_REQUEST_CODE);
                } else {
                    showError(R.string.storage_permission_error);
                }
                break;
        }
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
        cacheParticipantTeam(team);
        Log.d(TAG, "New team " + team.getName() + " created");

        String participantId = SharedPreferencesUtil.getMyParticipantId();
        presenter.assignParticipantToTeam(participantId, team.getId());
    }

    @Override
    public void participantJoinedTeam(@NotNull String participantId, int teamId) {
        SharedPreferencesUtil.saveMyTeamId(teamId);
        teamCreated = true;
        showTeamCreatedView();
    }

    @Override
    public void onAssignParicipantToTeamError(@NotNull Throwable error, @NotNull String participantId, int teamId) {
        showError(R.string.team_created_but_participant_team_join_error);
    }

    @Override
    public void onCreateTeamError(@NotNull Throwable error) {
        if (error instanceof IOException) {
            showNetworkErrorMessage(R.string.events_data_error);
        } else {
            showError(R.string.teams_data_error, error.getMessage(), null);
        }
    }

    @Override
    public void showCreateTeamConstraintError() {
        if (teamNameInputLayout != null) {
            teamNameInputLayout.setError(getString(R.string.duplicate_team_name_error));
            showError(getString(R.string.create_team_title), getString(R.string.duplicate_team_name_error), null);
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

    public void editTeamImage() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, SELECT_TEAM_IMAGE_GALlERY_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        teamProfileIv = createTeamCard.findViewById(R.id.team_image);
        teamProfileIv.setOnClickListener(onClickListener);
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
        teamCreatedCard.setVisibility(View.INVISIBLE);
    }

    void showTeamCreatedView() {
        teamCreatedCard.setVisibility(View.VISIBLE);
        createTeamCard.setVisibility(View.INVISIBLE);
    }

    void enableCreateTeamButton() {
        boolean enabled = false;
        if (teamNameEditText != null) {
            String teamName = teamNameEditText.getText().toString();
            if (teamName.trim().length() >= Constants.MIN_TEAM_NAME_CHAR_LENGTH) {
                enabled = true;
            }
        }
        createTeamButton.setEnabled(enabled);
    }

}
