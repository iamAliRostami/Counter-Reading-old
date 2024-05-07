package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.COMPLETELY_DELETE;
import static com.leon.counter_reading.enums.NotificationType.SAVE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD_TEMP;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME_TEMP;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CalendarTool.getDate;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDeleteBinding;
import com.leon.counter_reading.utils.Crypto;
import com.leon.counter_reading.utils.CustomToast;

import org.jetbrains.annotations.NotNull;

public class DeleteFragment extends DialogFragment implements View.OnClickListener {
    private String trackId;
    private boolean completelyDelete;
    private FragmentDeleteBinding binding;
    private Activity activity;

    public DeleteFragment() {
    }

    public static DeleteFragment newInstance(String id, boolean completelyDelete) {
        final DeleteFragment fragment = new DeleteFragment();
        final Bundle args = new Bundle();
        args.putString(BILL_ID.getValue(), id);
        args.putBoolean(COMPLETELY_DELETE.getValue(), completelyDelete);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trackId = getArguments().getString(BILL_ID.getValue());
            completelyDelete = getArguments().getBoolean(COMPLETELY_DELETE.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        makeRing(activity, SAVE);
        setOnClickListener();
    }

    private void setOnClickListener() {
        binding.buttonSubmit.setOnClickListener(this);
        binding.buttonClose.setOnClickListener(this);
        binding.imageViewPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.image_view_password) {
            if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            } else
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (id == R.id.button_close) {
            dismiss();
        } else if (id == R.id.button_submit) {
            if (checkInput(binding.editTextUsername) && checkInput(binding.editTextPassword)) {
                String password = Crypto.encrypt(binding.editTextPassword.getText().toString());
                String username = binding.editTextUsername.getText().toString().toLowerCase();
                if (getApplicationComponent().SharedPreferenceModel()
                        .getStringData(USERNAME_TEMP.getValue()).equals(username) &&
                        getApplicationComponent().SharedPreferenceModel()
                                .getStringData(PASSWORD_TEMP.getValue()).equals(password)) {
                    if (trackId.isEmpty()) {
                        if (completelyDelete)
                            getApplicationComponent().MyDatabase().trackingDao().deleteTrackingDtosCompletely();
                        else
                            getApplicationComponent().MyDatabase().trackingDao()
                                    .updateTrackingDtoByArchive(true, false, true, getDate(requireActivity()));
                    } else if (completelyDelete)
                        getApplicationComponent().MyDatabase().trackingDao().deleteTrackingDtosCompletely(trackId);
                    else
                        getApplicationComponent().MyDatabase().trackingDao()
                                .updateTrackingDtoByArchive(trackId, true, false, true, getDate(requireActivity()));
                    Intent intent = activity.getIntent();
                    activity.finish();
                    startActivity(intent);
                } else {
                    new CustomToast().warning(getString(R.string.error_is_not_match));
                }
            }
        }
    }

    private boolean checkInput(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getString(R.string.error_empty));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}